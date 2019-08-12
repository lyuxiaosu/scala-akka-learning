import akka.actor._
import akka.cluster._
import akka.persistence._
import akka.pattern._
import scala.concurrent.duration._


object POSTerminal {
  case class Fruit(code: String, name: String, price: Double)
  case class Item(fruit: Fruit, qty: Int)

  sealed trait Command {
	def getname:String
  }
  case class Checkout(fruit: Fruit, qty: Int) extends Command {
	def getname = {
		"Checkout"
	}
  }
  case object ShowTotol extends Command {
	def getname = {
		"ShowTotol"
	}
  }
  case class PayCash(amount: Double) extends Command {
	def getname = {
		"PayCash"
	}
  }
  case object Shutdown extends Command {
	def getname = {
		"Shutdown"
	}
  }

  sealed trait Event {}
  case class ItemScanned(fruit: Fruit, qty: Int) extends Event
  case object Paid extends Event

  case class Items(items: List[Item] = Nil) {
    def itemAdded(evt: Event): Items = evt match {
      case ItemScanned(fruit,qty) =>
        copy( Item(fruit,qty) :: items )   //append item

      case _ => this     //nothing happens
    }
    def billPaid = copy(Nil)     //clear all items
    override def toString = items.reverse.toString()
  }

  def termProps = Props(new POSTerminal())

  //backoff suppervisor  must use onStop mode
  def POSProps: Props = {
    val options = Backoff.onStop(
      childProps = termProps,
      childName = "posterm",
      minBackoff = 1 second,
      maxBackoff = 5 seconds,
      randomFactor = 0.20
    )
    BackoffSupervisor.props(options)
  }

}

class POSTerminal extends PersistentActor with ActorLogging {
  import POSTerminal._
  val cluster = Cluster(context.system)


  println("------------------------------------I am created---------------------" + this)
  // self.path.parent.name is the type name (utf-8 URL-encoded)
  // self.path.name is the entry identifier (utf-8 URL-encoded)  but entity has a supervisor
  override def persistenceId: String = self.path.parent.parent.name + "-" + self.path.parent.name

  var currentItems = Items()


  override def receiveRecover: Receive = {
    case evt: Event => currentItems = currentItems.itemAdded(evt)
      log.info(s"*****  ${persistenceId} recovering events ...  ********")
    case SnapshotOffer(_,loggedItems: Items) =>
      log.info(s"*****  ${persistenceId} recovering snapshot ...  ********")
      currentItems = loggedItems
  }

  override def receiveCommand: Receive = {
    case Checkout(fruit,qty) =>
      log.info(s"*********${persistenceId} is scanning item: $fruit, qty: $qty *********")
      persist(ItemScanned(fruit,qty))(evt =>  currentItems = currentItems.itemAdded(evt))

    case ShowTotol =>
      log.info(s"*********${persistenceId} on ${cluster.selfAddress} has current scanned items: *********")
      if (currentItems.items == Nil)
        log.info(s"**********${persistenceId} None transaction found! *********")
      else
        currentItems.items.reverse.foreach (item =>
          log.info(s"*********${persistenceId}: ${item.fruit.name} ${item.fruit.price} X ${item.qty} = ${item.fruit.price * item.qty} *********"))

    case PayCash(amt) =>
      log.info(s"**********${persistenceId} paying $amt to settle ***********")
      persist(Paid) { _ =>
        currentItems = currentItems.billPaid
        saveSnapshot(currentItems)     //no recovery
      }

    //shutdown this node to validate entity relocation and proper state recovery
    case Shutdown =>
      log.info(s"******** node ${cluster.selfAddress} is leaving cluster ... *******")
      cluster.leave(cluster.selfAddress)
  }
}

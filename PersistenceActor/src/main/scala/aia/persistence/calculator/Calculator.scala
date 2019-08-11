package persistence.calculator
import akka.actor._
import akka.persistence._

object Calculator {
  sealed trait Command
  case class Operand(x: Int) extends Command
  case class Add(x: Int) extends Command
  case class Sub(x: Int) extends Command
  case class Mul(x: Int) extends Command
  case class Div(x: Int) extends Command
  case class ShowResult(x: Double) extends Command
  case object BackupResult extends Command    //saveSnapshot

  sealed trait Event
  case class SetNum(x: Int) extends Event
  case class Added(x: Int) extends Event
  case class Subtracted(x: Int) extends Event
  case class Multiplied(x: Int) extends Event
  case class Divided(x: Int) extends Event


  case class State(result: Int) {
    def updateState(evt: Event): State = evt match {
      case SetNum(x) => copy(result = x)
      case Added(x) => copy(result = this.result + x)
      case Subtracted(x) => copy(result = this.result - x)
      case Multiplied(x) => copy(result = this.result * x)
      case Divided(x) => copy(result = this.result / x)
    }
  }

  case class LogMessage(msg: String)       //broadcase message type
  def props = Props(new Calculator)
}
class Calculator extends PersistentActor with ActorLogging {
  import Calculator._
  var state: State = State(0)

  override def persistenceId: String = "persistence-actor"
  val snapShotInterval = 5
  override def receiveCommand: Receive = {
    case Operand(x) => persist(SetNum(x))(handleEvent)
    case Add(x) => persist(Added(x))(handleEvent)
    case Sub(x) => persist(Subtracted(x))(handleEvent)
    case Mul(x) => persist(Multiplied(x))(handleEvent)
    case Div(x) if (x != 0) => persist(Divided(x))(handleEvent)

    case ShowResult =>
      context.system.eventStream.publish(LogMessage(s"Current state: $state"))
    case BackupResult =>
      saveSnapshot(state)
      context.system.eventStream.publish(LogMessage(s"Manual saving snapshot: $state"))

    case SaveSnapshotSuccess(metadata) =>
      context.system.eventStream.publish(LogMessage(s"Successfully saved state: $state"))
    case SaveSnapshotFailure(metadata, reason) =>
      context.system.eventStream.publish(LogMessage(s"Saving state: $state failed!"))

  }
  def handleEvent(evt: Event) = {   //update state and publish progress
    state = state.updateState(evt)
    context.system.eventStream.publish(LogMessage(s"Producing event: $evt"))
    if (lastSequenceNr % snapShotInterval == 0 && lastSequenceNr != 0) {
      saveSnapshot(state)
      context.system.eventStream.publish(LogMessage(s"Saving snapshot: $state after $snapShotInterval events"))
    }
  }
  override def receiveRecover: Receive = {
    case evt: Event => {
      state = state.updateState(evt)
      context.system.eventStream.publish(LogMessage(s"Restoring event: $evt"))
    }
    case SnapshotOffer(mdata, sts: State) => {
      state = sts.copy(sts.result)
      context.system.eventStream.publish(LogMessage(s"Restoring snapshot: $mdata"))
    }
    case RecoveryCompleted => log.info(s"Recovery completed with starting state: $state")
  }

  override def onPersistRejected(cause: Throwable, event: Any, seqNr: Long): Unit = {
    log.info(s"Persistence Rejected: ${cause.getMessage}")
  }
  override def onPersistFailure(cause: Throwable, event: Any, seqNr: Long): Unit = {
    log.info(s"Persistence Error: ${cause.getMessage}")
  }
}

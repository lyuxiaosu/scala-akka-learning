package persistence.tracker
import akka.actor._
import persistence.calculator.Calculator
object EventTracker {
  def props = Props(new EventTracker)
}
class EventTracker extends Actor {
  override def preStart(): Unit = {
    context.system.eventStream.subscribe(self,classOf[Calculator.LogMessage])
    super.preStart()
  }

  override def postStop(): Unit = {
    context.system.eventStream.unsubscribe(self)
    super.postStop()
  }

  override def receive: Receive = {
    case Calculator.LogMessage(msg) => println(msg)
  }

}

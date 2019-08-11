package persistence.demo
import akka.actor._
import persistence.calculator.Calculator
import persistence.tracker.EventTracker
import persistence.leveldb.LevelDB

object persistenceDemo extends App {
  val persistenceSystem = ActorSystem("persistenceSystem")
 
  Thread.sleep(1000) 
  persistenceSystem.actorOf(EventTracker.props,"stateTeller")

  //create leveldb
  val port = 2552
  LevelDB.startupSharedJournal(persistenceSystem, (port == 2552), path =
        ActorPath.fromString("akka.tcp://persistenceSystem@127.0.0.1:2552/user/store"))

  val calculator = persistenceSystem.actorOf(Calculator.props,"Calculator")

  calculator ! Calculator.Add(3)
  calculator ! Calculator.Add(7)
  calculator ! Calculator.Mul(3)
  calculator ! Calculator.Div(2)
  calculator ! Calculator.Sub(8)
  calculator ! Calculator.Mul(12)
  calculator ! Calculator.ShowResult

  scala.io.StdIn.readLine()

  persistenceSystem.terminate()

}

import aia.cluster.SingletonActor._
import SingletonUser._
object SingletonDemo extends App {

  createSingleton(2551)    //seednode
  createSingleton(2552)
  createSingleton(2553)
  createSingleton(2554)

  scala.io.StdIn.readLine()

  sendToSingleton(Greeting("hello from tiger"))
  scala.io.StdIn.readLine()

  sendToSingleton(Relocate)
  scala.io.StdIn.readLine()

  sendToSingleton(Greeting("hello from cat"))
  scala.io.StdIn.readLine()

  sendToSingleton(Die)
  scala.io.StdIn.readLine()

}

import POSTerminal._
import POSShard._

object POSDemo extends App {
  POSShard.create(2552)
  Thread.sleep(1000)
  POSShard.create(2555)
  
  POSShard.create(2553)
  val posref = POSShard.create(2554)
  println("----------------------1. please click any botton to continue-----------------")
  scala.io.StdIn.readLine()

  val apple = Fruit("0001","high grade apple",10.5)
  val orange = Fruit("0002","sunkist orage",12.0)
  val grape = Fruit("0003","xinjiang red grape",15.8)

  posref ! POSCommand(1021, Checkout(apple,2))

  posref ! POSCommand(1021,Checkout(grape,1))

  posref ! POSCommand(1021,ShowTotol)
  println("----------------------2. please click any botton to continue-----------------")
  scala.io.StdIn.readLine()

  posref ! POSCommand(1021,Shutdown)
  println("----------------------3. please click any botton to continue-----------------")
  scala.io.StdIn.readLine()

  posref ! POSCommand(1021,Checkout(orange,10))


  posref ! POSCommand(1021,ShowTotol)
  println("----------------------4. please click any botton to continue-----------------")
  scala.io.StdIn.readLine()

  posref ! POSCommand(1028,Checkout(orange,10))

  posref ! POSCommand(1028,ShowTotol)
  scala.io.StdIn.readLine()



}

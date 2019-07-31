package aia.cluster

import akka.actor._
import com.typesafe.config.ConfigFactory
import akka.cluster.singleton.ClusterSingletonManager;
import akka.cluster.singleton.ClusterSingletonManagerSettings;

class SingletonActor extends Actor with ActorLogging {
  import SingletonActor._
  import akka.cluster._
  override def receive: Receive = {
    case Greeting(msg) =>
      log.info("*********got {} from {}********", msg, sender().path.address)
    case Relocate =>
      log.info("*********I'll move from {}********", self.path.address)
      val cluster = Cluster(context.system)
      cluster.leave(cluster.selfAddress)
    case Die =>
      log.info("*******I'm shutting down ... ********")
      self ! PoisonPill
  }
}

object SingletonActor {
  trait SingletonMsg {}
  case class Greeting(msg: String) extends SingletonMsg
  case object Relocate extends SingletonMsg
  case object Die extends SingletonMsg

  def props = Props(new SingletonActor)

  def createSingleton(port: Int) = {
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.parseString("akka.cluster.roles=[singleton]"))
      .withFallback(ConfigFactory.load())
    val singletonSystem = ActorSystem("ClusterSingletonSystem",config)

    val singletonManager = singletonSystem.actorOf(ClusterSingletonManager.props(
      singletonProps = props,
      terminationMessage = Die,
      settings = ClusterSingletonManagerSettings(singletonSystem)
        .withRole(Some("singleton"))   //.......singleton....
    ),
      name = "singletonManager"
    )
  }

}

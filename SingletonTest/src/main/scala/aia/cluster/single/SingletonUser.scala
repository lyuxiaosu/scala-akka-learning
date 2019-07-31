import com.typesafe.config.ConfigFactory
import akka.actor._
import akka.cluster.singleton._

object SingletonUser {
  import aia.cluster.SingletonActor._
  def sendToSingleton(msg: SingletonMsg) = {
    val config = ConfigFactory.parseString("akka.cluster.roles=[greeter]")
      .withFallback(ConfigFactory.load())

    val system = ActorSystem("ClusterSingletonSystem",config)
    val singletonProxy = system.actorOf(ClusterSingletonProxy.props(
      "user/singletonManager",
      ClusterSingletonProxySettings(system).withRole(None)
    ))

    singletonProxy ! msg
  }
}

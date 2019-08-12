import akka.actor._
import akka.cluster.sharding._
import com.typesafe.config.ConfigFactory
import persistence.leveldb.LevelDB

object POSShard {
 import POSTerminal._

 val shardName = "POSManager"
 case class POSCommand(id: Long, cmd: Command) {
   def shopId = id.toString.head.toString
   //def posId = id.toString
   def posId = cmd.getname
 }

 val getPOSId: ShardRegion.ExtractEntityId =  {
   case posCommand: POSCommand => (posCommand.posId, posCommand.cmd)
 }
 val getShopId: ShardRegion.ExtractShardId = {
   case posCommand: POSCommand => posCommand.shopId
 }


 def create(port: Int) = {
   val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
     .withFallback(ConfigFactory.load())
   val system = ActorSystem("posSystem",config)

   //create leveldb
   LevelDB.startupSharedJournal(system, (port == 2552), path =
        ActorPath.fromString("akka.tcp://posSystem@127.0.0.1:2552/user/store"))
 
   ClusterSharding(system).start(
     typeName = shardName,
     entityProps = POSProps,
     settings = ClusterShardingSettings(system),
     extractEntityId = getPOSId,
     extractShardId = getShopId
   )
 }

}

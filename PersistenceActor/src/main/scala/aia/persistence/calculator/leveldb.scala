
package persistence.leveldb

import akka.actor._
import akka.cluster._
import akka.persistence._
import com.typesafe.config.ConfigFactory
//import akka.cluster.singleton._
import scala.concurrent.duration._
import akka.persistence.journal.leveldb._
import akka.util.Timeout
import akka.pattern._

object LevelDB {

	def startupSharedJournal(system: ActorSystem, startStore: Boolean, path: ActorPath): Unit = {
		// Start the shared journal one one node (don't crash this SPOF)
		// This will not be needed with a distributed journal
		if (startStore)
			system.actorOf(Props[SharedLeveldbStore], "store")
				// register the shared journal
		import system.dispatcher
		implicit val timeout = Timeout(15.seconds)
		val f = (system.actorSelection(path) ? Identify(None))
		f.onSuccess {
			case ActorIdentity(_, Some(ref)) =>
				SharedLeveldbJournal.setStore(ref, system)
			case _ =>
				system.log.error("Shared journal not started at {}", path)
				system.terminate()
			}
		f.onFailure {
			case _ =>
				system.log.error("Lookup of shared journal at {} timed out", path)
				system.terminate()
		}
	}
}

package appfarm.model

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._

/**
 * The singleton that has methods for accessing the database
 */
object Push extends Push with LongKeyedMetaMapper[Push] {
  override def dbTableName = "pushs" // define the DB table name
}

class Push extends LongKeyedMapper[Push] with IdPK {
  def getSingleton = Push // what's the "meta" server

  object deviceId extends MappedString(this, 64)
  object firstId extends MappedString(this, 64)
  object secondId extends MappedString(this, 64)
  object thirdId extends MappedString(this, 64)

}

package appfarm.model

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._

/**
 * The singleton that has methods for accessing the database
 */
object Relation extends Relation with LongKeyedMetaMapper[Relation] {
  override def dbTableName = "relations" // define the DB table name
}

class Relation extends LongKeyedMapper[Relation] with IdPK {
  def getSingleton = Relation // what's the "meta" server

  object fromId extends MappedString(this, 64)
  object toId extends MappedString(this, 64)
  object status extends MappedString(this, 2)
}

package appfarm.model

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import xml.Node

/**
 * The singleton that has methods for accessing the database
 */
object Profile extends Profile with LongKeyedMetaMapper[Profile] {
  override def dbTableName = "profiles" // define the DB table name

//  override def screenWrap = Full(<lift:surround with="default" at="content">
//			       <lift:bind /></lift:surround>)
  // define the order fields will appear in forms and output
//  override def fieldOrder = List(id, firstName, lastName, email,
//  locale, timezone, password, textArea)

//  // comment this line out to require email validations
//  override def skipEmailValidation = true
}

class Profile extends LongKeyedMapper[Profile] with IdPK {
  def getSingleton = Profile // what's the "meta" server

  object deviceId extends MappedString(this, 64)

//  object timestamp extends MappedString(this, 4 +2+2 +2+2+2+2)
  object name extends MappedString(this, 64)
  object gender extends MappedString(this, 4)
  object age extends MappedInt(this)
  object region extends MappedString(this, 32)
  object blood extends MappedString(this, 4)
  object height extends MappedInt(this)
  object job extends MappedString(this, 32)
  object org extends MappedString(this, 32)
  object hobby extends MappedString(this, 32)
  object skill extends MappedString(this, 32)
  object interest extends MappedString(this, 128)
  object superior extends MappedString(this, 128)
  object motto extends MappedString(this, 128)

}

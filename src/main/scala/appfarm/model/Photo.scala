package appfarm.model

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._

/**
 * The singleton that has methods for accessing the database
 */
object Photo extends Photo with LongKeyedMetaMapper[Photo] {
  override def dbTableName = "photos" // define the DB table name

//  override def screenWrap = Full(<lift:surround with="default" at="content">
//			       <lift:bind /></lift:surround>)
  // define the order fields will appear in forms and output
//  override def fieldOrder = List(id, firstName, lastName, email,
//  locale, timezone, password, textArea)

//  // comment this line out to require email validations
//  override def skipEmailValidation = true
}

class Photo extends LongKeyedMapper[Photo] with IdPK {
  def getSingleton = Photo // what's the "meta" server

  object deviceId extends MappedString(this, 64)

//  object timestamp extends MappedString(this, 4 +2+2 +2+2+2+2)
  object file1 extends MappedString(this, 96)
  object file2 extends MappedString(this, 96)
  object file3 extends MappedString(this, 96)
  object file4 extends MappedString(this, 96)
  object file5 extends MappedString(this, 96)
  object file6 extends MappedString(this, 96)

}

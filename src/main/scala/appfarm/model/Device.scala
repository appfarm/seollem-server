package appfarm {
package model {

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._

/**
 * The singleton that has methods for accessing the database
 */
object Device extends Device with KeyedMetaMapper[String, Device] {
  override def dbTableName = "devices" // define the DB table name

  // define the order fields will appear in forms and output
//  override def fieldOrder = List(id, firstName, lastName, email,
//  locale, timezone, password, textArea)

//  // comment this line out to require email validations
//  override def skipEmailValidation = true
}

class Device extends KeyedMapper[String, Device] {
  def getSingleton = Device // what's the "meta" server

  def primaryKeyField = deviceId

  object deviceId extends MappedStringIndex(this, 64) {
    override def writePermission_? = true
    override def dbAutogenerated_? = false
    override def dbNotNull_? = true
    override def dbIndexed_? = true
  }

//  object timestamp extends MappedDateTime(this)
  object profileId extends MappedLongForeignKey(this, Profile)
  object photoId extends MappedLongForeignKey(this, Photo)
  object photoCount extends MappedInt(this)

}

}
}

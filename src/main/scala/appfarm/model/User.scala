package appfarm {
package model {

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._

/**
 * The singleton that has methods for accessing the database
 */
object User extends User with KeyedMetaMapper[String, User] {
  override def dbTableName = "users" // define the DB table name


//  override def screenWrap = Full(<lift:surround with="default" at="content">
//			       <lift:bind /></lift:surround>)
  // define the order fields will appear in forms and output
//  override def fieldOrder = List(id, firstName, lastName, email,
//  locale, timezone, password, textArea)

//  // comment this line out to require email validations
//  override def skipEmailValidation = true
}

class User extends KeyedMapper[String, User] {
  def getSingleton = User // what's the "meta" server

  def primaryKeyField = deviceId

  object deviceId extends MappedStringIndex(this, 64) {
    override def writePermission_? = true
    override def dbAutogenerated_? = false
    override def dbNotNull_? = true
    override def dbIndexed_? = true
  }

  object timestamp extends MappedString(this, 4 +2+2 +2+2+2+2)
  object name extends MappedString(this, 64)
  object gender extends MappedString(this, 2)
  object birthday extends MappedString(this, 4 +2+2)
  object area extends MappedString(this, 32)
  object blood extends MappedString(this, 2)
  object height extends MappedInt(this)
  object job extends MappedString(this, 32)
  object org extends MappedString(this, 32)
  object urls extends MappedString(this, 128)

//timestamp
//name
//gender
//birthday
//area
//blood
//height
//job
//org
//urls

}

}
}




// original


//package appfarm {
//package model {
//
//import _root_.net.liftweb.mapper._
//import _root_.net.liftweb.util._
//import _root_.net.liftweb.common._
//
///**
// * The singleton that has methods for accessing the database
// */
//object User extends User with MetaMegaProtoUser[User] {
//  override def dbTableName = "users" // define the DB table name
//  override def screenWrap = Full(<lift:surround with="default" at="content">
//			       <lift:bind /></lift:surround>)
//  // define the order fields will appear in forms and output
//  override def fieldOrder = List(id, firstName, lastName, email,
//  locale, timezone, password, textArea)
//
//  // comment this line out to require email validations
//  override def skipEmailValidation = true
//}
//
///**
// * An O-R mapped "User" class that includes first name, last name, password and we add a "Personal Essay" to it
// */
//class User extends MegaProtoUser[User] {
//  def getSingleton = User // what's the "meta" server
//
//  // define an additional field for a personal essay
//  object textArea extends MappedTextarea(this, 2048) {
//    override def textareaRows  = 10
//    override def textareaCols = 50
//    override def displayName = "Personal Essay"
//  }
//}
//
//}
//}

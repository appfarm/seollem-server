package bootstrap.liftweb

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.http.provider._

import _root_.java.sql.{Connection, DriverManager}
import _root_.appfarm.model._

import _root_.net.liftweb.mapper._
import _root_.scala.xml._
import net.liftweb.http._

import _root_.net.liftweb.json._
import _root_.net.liftweb.json.JsonAST.{JArray, JBool, JField, 
                                        JInt, JObject, JString, JValue, render}
import _root_.net.liftweb.json.JsonDSL._
import _root_.net.liftweb.json.Printer._


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
    LiftRules.supplimentalHeaders = s => s.addHeaders(
      List(HTTPParam("X-Lift-Version", LiftRules.liftVersion),
        HTTPParam("Access-Control-Allow-Origin", "*"),
        HTTPParam("Access-Control-Allow-Credentials", "true"),
        HTTPParam("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS"),
        HTTPParam("Access-Control-Allow-Headers", "WWW-Authenticate,Keep-Alive,User-Agent,X-Requested-With,Cache-Control,Content-Type")
    ))


///*
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor = new StandardDBVendor(
        Props.get("db.driver") openOr "org.h2.Driver",
              Props.get("db.url") openOr "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
              Props.get("db.user"),
              Props.get("db.password"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    }
//*/

    // use Cloud Foundry Mysql service
//    if (!DB.jndiJdbcConnAvailable_?) DB.defineConnectionManager(DefaultConnectionIdentifier, DBVendor)


    // where to search snippet
    LiftRules.addToPackages("appfarm")
    Schemifier.schemify(true, Schemifier.infoF _, Device, Profile, Photo, File, Relation, Push)

    LiftRules.handleMimeFile = OnDiskFileParamHolder.apply

    LiftRules.statelessDispatchTable.append(MyRest);

  }

  /**
   * Force the request to be UTF-8
   */
  private def makeUtf8(req: HTTPRequest) {
    req.setCharacterEncoding("UTF-8")
  }

}


object DBVendor extends ConnectionManager {
  def newConnection(name: ConnectionIdentifier): Box[Connection] = {
    try {
      import org.cloudfoundry.runtime.env._
      import org.cloudfoundry.runtime.service.relational._
      Full(new MysqlServiceCreator(new CloudEnvironment()).createSingletonService().service.getConnection())

    } catch {
      case e : Exception => e.printStackTrace; Empty
    }
  }
  def releaseConnection(conn: Connection) {conn.close}
}


import net.liftweb.http._
import net.liftweb.http.rest._


object MyRest extends RestHelper {
  
  object Log extends Logger
  Log.info("zuns00: MyRest Created...")
  
  serve {

    case Req("help" :: _, "", GetRequest) => () => Full(XmlResponse(
      <apis>
      <code>1000</code><desc>upload profile</desc>
      <code>1010</code><desc>upload photo</desc>
      <code>1100</code><desc>get device</desc>
      <code>1110</code><desc>get profile</desc>
      <code>1120</code><desc>get photo</desc>
      <code>1130</code><desc>get today3</desc>
      <code>1200</code><desc>get photo - slot</desc>
      <code>1210</code><desc>get photo - filename</desc>
      <code>1300</code><desc>propose</desc>
      <code>1310</code><desc>say yes</desc>
      <code>1320</code><desc>say no</desc>

      <code>800</code><desc>devices</desc>
      <code>801</code><desc>profiles</desc>
      <code>802</code><desc>photos</desc>
      <code>803</code><desc>files</desc>
      <code>804</code><desc>push</desc>
      <code>805</code><desc>relations</desc>
      </apis>
    ))
    // REST API
    // Create  POST
    // Read    GET
    // Update  PUT
    // Delete  DELETE


    //deleteDevice DELETE device [deviceId]
    //uploadProfile POST profile [deviceId] :XMLBODY
    //uploadPhoto POST photo [deviceId] :BinaryBody
    //readDevice GET device [deviceId]
    //readProfile GET profile [deviceId]
    //readPhotoFilename GET photo [deviceId]
    //readPhotoByFilename GET photo [filename]
    //readPhotoBySlot GET photo [deviceId]  [slot#]
    //readToday3 GET today3 [deviceId]
    //sendPropose GET propose [fromId] [toId]
    //sendSayYes GET sayyes [fromId] [toId]
    //sendSayNo GET sayno [fromId] [toId]
    
//     case r @ Req("users" :: Nil, "", PostRequest) => () => createUser(r)
//     case     Req("users" :: deviceId :: Nil, "", GetRequest) => () => readUser(deviceId)
//     case r @ Req("users" :: deviceId :: Nil, "", PutRequest) => () => updateUser(deviceId)
// //    case r @ Req("users" :: deviceId :: Nil, "", DeleteRequest) => () => deleteUser(deviceId)
//     case     Req("users" :: deviceId :: "photos" :: Nil, "", GetRequest) => () => readPhotosURL(deviceId)
//     case r @ Req("users" :: deviceId :: "photos" :: Nil, "", PutRequest) => () => updatePhotos(deviceId)
//     case     Req("users" :: deviceId :: "photos" :: slot :: Nil, "", GetRequest) => () => readPhotosSlot(deviceId, slot)
//     
//     case     Req("users" :: deviceId :: "relations" :: Nil, "", GetRequest) => () => readRelations(deviceId)
//     case     Req("users" :: deviceId :: "relations" :: withId :: Nil, "", GetRequest) => () => readRelations(deviceId, withId)
//     case     Req("users" :: deviceId :: "propose" :: withId :: Nil, "", PutRequest) => () => createRelations(deviceId, withId)
//     case     Req("users" :: deviceId :: "sayyes" :: withId :: Nil, "", PutRequest) => () => updateRelationsYes(deviceId, withId)
//     case     Req("users" :: deviceId :: "sayno" :: withId :: Nil, "", PutRequest) => () => updateRelationsNo(deviceId, withId)
//     case     Req("users" :: deviceId :: "relations" :: withId :: Nil, "", DeleteRequest) => () => deleteRelations(deviceId, withId)
//     
//     case     Req("users" :: deviceId :: "today3" :: Nil, "", GetRequest) => () => readRelations(deviceId)
    
    // old api
    case     Req("device"  :: deviceId :: Nil, "",         DeleteRequest) => () => deleteDevice(deviceId)
    case r @ Req("profile" :: deviceId :: Nil, "",         PostRequest  ) => () => uploadProfile(r, deviceId)
    case r @ Req("photo"   :: deviceId :: Nil, "",         PostRequest  ) => () => uploadPhoto(r, deviceId)
    case r @ Req("photo"   :: Nil, "",                     PostRequest  ) => () => uploadPhoto(r) // test iphone upload
    case     Req("device"  :: deviceId :: Nil, "",         GetRequest   ) => () => readDevice(deviceId)
    case     Req("profile" :: deviceId :: Nil, "",         GetRequest   ) => () => readProfile(deviceId)
    case     Req("json" :: "profile" :: deviceId :: Nil, "",         GetRequest   ) => readProfileJson(deviceId)
    case     Req("photo"   :: deviceId :: Nil, "",         GetRequest   ) => () => readPhotoFilename(deviceId)
    case     Req("photo"   :: fileName :: Nil, extension,  GetRequest   ) => () => readPhotoByFilename(fileName, extension)
    case     Req("photo"   :: deviceId :: slot :: Nil, "", GetRequest   ) => () => readPhotoBySlot(deviceId, slot)
    case     Req("today3"  :: deviceId :: Nil, "",         GetRequest   ) => () => readToday3(deviceId)
    case     Req("propose" :: fromId   :: toId :: Nil, "", GetRequest   ) => () => sendPropose(fromId, toId)
    case     Req("sayyes"  :: fromId   :: toId :: Nil, "", GetRequest   ) => () => sendSayYes(fromId, toId)
    case     Req("sayno"   :: fromId   :: toId :: Nil, "", GetRequest   ) => () => sendSayNo(fromId, toId)

    //  1000    upload profile
    //  1010    upload photo
    //  1100    get device
    //  1110    get profile
    //  1120    get photo
    //  1130    get today3
    //  1200    get photo - slot
    //  1210    get photo - filename
    //  1300    propose
    //  1310    say yes
    //  1320    say no

    case request @ Req("1000" :: deviceId :: _, "", PostRequest) => () => res1000_upload_profile(request, deviceId)
    case request @ Req("1010" :: deviceId :: _, "", PostRequest) => res1010_upload_photo(request, deviceId)
    case Req("1100" :: deviceId :: _, "", GetRequest) => () => res1100_get_device(deviceId)
    case Req("1110" :: deviceId :: _, "", GetRequest) => () => res1110_get_profile(deviceId)
    case Req("1120" :: deviceId :: _, "", GetRequest) => () => res1120_get_photo(deviceId)
    case Req("1130" :: deviceId :: _, "", GetRequest) => () => res1130_get_today3(deviceId)
    case Req("1200" :: deviceId :: slot :: _, "", GetRequest) => () => res1200_get_photo_slot(deviceId, slot)
    case Req("1210" :: filename :: _, extension, GetRequest) => () => res1210_get_photo_filename(filename, extension)
    case Req("1300" :: deviceId :: destId :: _, "", GetRequest) => () => res1300_propose(deviceId, destId)
    case Req("1310" :: deviceId :: destId :: _, "", GetRequest) => () => res1310_say_yes(deviceId, destId)
    case Req("1320" :: deviceId :: destId :: _, "", GetRequest) => () => res1320_say_no(deviceId, destId)

    // FOR DEBUG
    // ----------------
    // 800  Device
    // 801  Profile
    // 802  Photo
    // 803  File
    // 804  Push
    // 805  Relation
    case Req("800" :: _, "", GetRequest) => () => Full(InMemoryResponse(Device.findAll.toString.getBytes("UTF-8"), List("Content-Type" -> "text/html"), Nil, 200))
    case Req("801" :: _, "", GetRequest) => () => Full(InMemoryResponse(Profile.findAll.toString.getBytes("UTF-8"), List("Content-Type" -> "text/html"), Nil, 200))
    case Req("802" :: _, "", GetRequest) => () => Full(InMemoryResponse(Photo.findAll.toString.getBytes("UTF-8"), List("Content-Type" -> "text/html"), Nil, 200))
    case Req("803" :: _, "", GetRequest) => () => Full(InMemoryResponse(File.findAll.toString.getBytes("UTF-8"), List("Content-Type" -> "text/html"), Nil, 200))
    case Req("804" :: _, "", GetRequest) => () => Full(InMemoryResponse(Push.findAll.toString.getBytes("UTF-8"), List("Content-Type" -> "text/html"), Nil, 200))
    case Req("805" :: _, "", GetRequest) => () => Full(InMemoryResponse(Relation.findAll.toString.getBytes("UTF-8"), List("Content-Type" -> "text/html"), Nil, 200))
  }

  def deleteDevice(deviceId: String) : Box[LiftResponse] = {Full(XmlResponse(<todo>TODO</todo>))} //TODO: deleteDevice
  def uploadProfile(r: Req, deviceId: String) : Box[LiftResponse] = {res1000_upload_profile(r,deviceId)}
  def uploadPhoto(r: Req) : Box[LiftResponse] = {res1010_upload_photo(r)}
  def uploadPhoto(r: Req, deviceId: String) : Box[LiftResponse] = {res1010_upload_photo(r,deviceId)}
  def readDevice(deviceId: String) : Box[LiftResponse] = {res1100_get_device(deviceId)}
  def readProfile(deviceId: String) : Box[LiftResponse] = {res1110_get_profile(deviceId)}
  def readPhotoFilename(deviceId: String) : Box[LiftResponse] = {res1120_get_photo(deviceId)}
  def readPhotoByFilename(filename: String, extension: String) : Box[LiftResponse] = {res1210_get_photo_filename(filename, extension)}
  def readPhotoBySlot(deviceId: String, slot: String) : Box[LiftResponse] = {res1200_get_photo_slot(deviceId, slot)}
  def readToday3(deviceId: String) : Box[LiftResponse] = {Full(XmlResponse(<todo>TODO</todo>))} //TODO: readToday3
  def sendPropose(fromId: String, toId: String) : Box[LiftResponse] = {res1300_propose(fromId, toId)}
  def sendSayYes(fromId: String, toId: String) : Box[LiftResponse] = {res1310_say_yes(fromId, toId)}
  def sendSayNo(fromId: String, toId: String) : Box[LiftResponse] = {res1320_say_no(fromId, toId)}

  def readProfileJson(deviceId: String) = {

    def profileToJson(p : Profile) = JObject(List(
      JField("name", JString(p.name)),
      JField("gender", JString(p.gender)),
      JField("age", JInt(BigInt(p.age))),
      JField("region", JString(p.region)),
      JField("blood", JString(p.blood)),
      JField("height", JInt(BigInt(p.height))),
      JField("job", JString(p.job)),
      JField("org", JString(p.org)),
      JField("hobby", JString(p.hobby)),
      JField("skill", JString(p.skill)),
      JField("interest", JString(p.interest)),
      JField("superior", JString(p.superior)),
      JField("motto", JString(p.motto))
    ))

    Device.findByKey(deviceId) match {
      case Full(d) =>
        Profile.findByKey(d.profileId) match {
          case Full(p) => JObject(List(
            JField("response", JInt(2110)), 
            JField("desc", JString("profile response ok")),
            JField("profile", profileToJson(p))
          ))
    
          case _ => JObject(List(JField("response", JInt(3111)), JField("desc", JString("profile not found"))))
        }
      case _ => JObject(List(JField("response", JInt(3110)), JField("desc", JString("device not found"))))
    }

    // JObject(List(JField("result", JString("fail"))))
    // JObject(List(JField("name", JString(p.name))))
  }

  // upload profile
  def res1000_upload_profile(request: Req, deviceId: String) : Box[LiftResponse] = {
    /**
     * Takes in plist key-value format and returns a Map[String, Seq[Node]]
     */
    def plistToMap(nodes:Seq[Node]) = {
      nodes.grouped(2).map {
        case Seq(keyNode, elementNode) => (keyNode.text, elementNode)
      }.toMap
    }

    def createProfileFromXML(map:Map[String, Node], device: Device) : Profile = {
      val profile = new Profile

      profile.deviceId(device.deviceId)
      profile.name(map.get("name").get.text)
      profile.gender(map.get("gender").get.text)
      profile.age(Integer.parseInt((map.get("age").get.text), 10))
      profile.region(map.get("region").get.text)
      profile.blood(map.get("blood").get.text)
      profile.height(Integer.parseInt((map.get("height").get.text), 10))
      profile.job(map.get("job").get.text)
      profile.org(map.get("org").get.text)
      profile.hobby(map.get("hobby").get.text)
      profile.skill(map.get("skill").get.text)
      profile.interest(map.get("interest").get.text)
      profile.superior(map.get("superior").get.text)
      profile.motto(map.get("motto").get.text)

      profile.save

      return profile
    }

    // check deviceId
    val device = Device.findByKey(deviceId) match {
      case Full(d) => d   // device exist
      case _ => Device.create.deviceId(deviceId).photoCount(0)  // new device
    }

    // create new profile
    val reqMap = plistToMap(request.xml.get \\ "dict" \ "_")
    val profileMap = plistToMap(reqMap.get("profile").get \\ "dict" \ "_")
    val profile = createProfileFromXML(profileMap, device)
    device.profileId(profile)

    // update device
    device.save

    // TODO: 2000, 2001 res
    // return result xml
    Full(XmlResponse(
      <plist version="1.0">
        <dict>
          <key>response</key><integer>2000</integer>
          <key>desc</key><string>device created, profile inserted</string>;
          <key>profileId</key><long>{device.profileId}</long>
        </dict>
      </plist>
    ))
  }

  // upload photo
  def res1010_upload_photo(request: Req) : Box[LiftResponse] = {
    // check deviceId
    Log.debug("deviceId: " + (S.param("deviceId") openOr "empty"))
    val deviceId = S.param("deviceId") openOr ""
    res1010_upload_photo(request, deviceId)
  }

  // upload photo
  def res1010_upload_photo(request: Req, deviceId: String) : Box[LiftResponse] = {
    // check deviceId
    Device.findByKey(deviceId) match {
      case Full(device) => {
        // input name="myFileX" X -> photo slot (1~6)
        val photo = Photo.findByKey(device.photoId) match {
          case Full(p) => Photo.create.file1(p.file1).file2(p.file2).file3(p.file3).file4(p.file4).file5(p.file5).file6(p.file6)
          case _ => new Photo
        }

        Log.debug(request.uploadedFiles)
        for(fp@FileParamHolder(inputTagName, _,_,_) <- request.uploadedFiles if inputTagName.startsWith("myFile")) {
          if (fp.mimeType.startsWith("image/")) {
            device.photoCount(device.photoCount + 1)
            val name = deviceId + device.photoCount + "." + fp.mimeType.substring(fp.mimeType.lastIndexOf("/")+1)
            val file = File.create.name(name).content(fp.file).saveMe
            inputTagName.last match {
              case '1' => photo.file1(name)
              case '2' => photo.file2(name)
              case '3' => photo.file3(name)
              case '4' => photo.file4(name)
              case '5' => photo.file5(name)
              case '6' => photo.file6(name)
              case _ => file.delete_!
            }
            println("!!!!!!" + fp.mimeType + fp.fileName + "|" + name + "|" + device.photoCount)
          } else println("!!!!!! not image/XXX" + fp.mimeType + fp.fileName + "|" + device.photoCount)
//        println("Here!!!! " + S.param("myParam") + " " + new String(fp.file, "UTF-8"))
        }
        photo.save
        device.photoId(photo).save
        Full(XmlResponse(
          <plist version="1.0">
            <dict>
              <key>response</key>
              <integer>2010</integer>
              <key>desc</key>
              <string>successfully uploaded</string>
              <key>photoId</key>
              <long>{device.photoId}</long>
            </dict>
          </plist>
        ))
      }

      case _ => Full(XmlResponse(
        <plist version="1.0">
          <dict>
            <key>response</key>
            <integer>3010</integer>
            <key>desc</key>
            <string>device not found</string>
          </dict>
        </plist>
      ))
    }
  }

  // get device
  def res1100_get_device(deviceId: String) : Box[LiftResponse] = {

    def deviceToXML(d : Device) : Node = {
      <dict>
        <key>deviceId</key><string>{d.deviceId}</string>
        <key>profileId</key><long>{d.profileId}</long>
        <key>photoId</key><long>{d.photoId}</long>
        <key>photoCount</key><integer>{d.photoCount}</integer>
      </dict>
    }

    Device.findByKey(deviceId) match {
      case Full(d) => Full(XmlResponse(
        <plist version="1.0">
          <dict>
            <key>response</key><integer>2100</integer>
            <key>desc</key><string>response recent profileId, photoId</string>
            <key>device</key>{deviceToXML(d)}
          </dict>
        </plist>
      ))
      case _ => Full(XmlResponse(
        <plist version="1.0">
          <dict>
            <key>response</key><integer>3100</integer>
            <key>desc</key><string>device not found</string>
          </dict>
        </plist>
      ))
    }
  }

  // get profile
  def res1110_get_profile(deviceId: String) : Box[LiftResponse] = {

    def profileToXML(p : Profile) : Node = {
      <dict>
        <key>name</key>     <string>{p.name}</string>
        <key>gender</key>   <string>{p.gender}</string>
        <key>age</key>      <integer>{p.age}</integer>
        <key>region</key>   <string>{p.region}</string>
        <key>blood</key>    <string>{p.blood}</string>
        <key>height</key>   <integer>{p.height}</integer>
        <key>job</key>      <string>{p.job}</string>
        <key>org</key>      <string>{p.org}</string>
        <key>hobby</key>    <string>{p.hobby}</string>
        <key>skill</key>    <string>{p.skill}</string>
        <key>interest</key> <string>{p.interest}</string>
        <key>superior</key> <string>{p.superior}</string>
        <key>motto</key>    <string>{p.motto}</string>
      </dict>
    }

    Device.findByKey(deviceId) match {
      case Full(d) =>
        Profile.findByKey(d.profileId) match {
          case Full(p) => Full(XmlResponse(
            <plist version="1.0">
              <dict>
                <key>response</key><integer>2110</integer>
                <key>desc</key><string>profile response ok</string>
                <key>profile</key>{profileToXML(p)}
              </dict>
            </plist>
          ))

          case _ => Full(XmlResponse(
            <plist version="1.0">
              <dict>
                <key>response</key><integer>3111</integer>
                <key>desc</key><string>profile not found</string>
              </dict>
            </plist>
          ))
        }
      case _ => Full(XmlResponse(
        <plist version="1.0">
          <dict>
            <key>response</key><integer>3110</integer>
            <key>desc</key><string>device not found</string>
          </dict>
        </plist>
      ))
    }
  }

  // get photo
  def res1120_get_photo(deviceId: String) : Box[LiftResponse] = {

    def photoToXML(p : Photo) : Node = {
      <dict> 
        <key>file1</key> <string>{p.file1}</string>
        <key>file2</key> <string>{p.file2}</string>
        <key>file3</key> <string>{p.file3}</string>
        <key>file4</key> <string>{p.file4}</string>
        <key>file5</key> <string>{p.file5}</string>
        <key>file6</key> <string>{p.file6}</string>
      </dict>
    }

    Device.findByKey(deviceId) match {
      case Full(d) =>
        Photo.findByKey(d.photoId) match {
          case Full(p) => Full(XmlResponse(
            <plist version="1.0">
              <dict>
                <key>response</key><integer>2120</integer>
                <key>desc</key><string>photo response ok</string>
                <key>photo</key>{photoToXML(p)}
              </dict>
            </plist>
          ))

          case _ => Full(XmlResponse(
            <plist version="1.0">
              <dict>
                <key>response</key><integer>3121</integer>
                <key>desc</key><string>profile not found</string>
              </dict>
            </plist>
          ))
        }
      case _ => Full(XmlResponse(
        <plist version="1.0">
          <dict>
            <key>response</key><integer>3120</integer>
            <key>desc</key><string>device not found</string>
          </dict>
        </plist>
      ))
    }
  }

  // get today3
  def res1130_get_today3(deviceId: String) : Box[LiftResponse] = {
    Full(XmlResponse(<todo>To Be Implement</todo>))
  }

  // get photo by slot
  def res1200_get_photo_slot(deviceId: String, slot: String) : Box[LiftResponse] = {

    // device
    Device.findByKey(deviceId) match {
      case Full(device) => {

        // device -> photo
        Photo.findByKey(device.photoId) match {
          case Full(photo) => {
            val filename = slot match {
              case "1" => photo.file1
              case "2" => photo.file2
              case "3" => photo.file3
              case "4" => photo.file4
              case "5" => photo.file5
              case "6" => photo.file6
              case _ => return Full(XmlResponse(
                <plist version="1.0">
                  <dict>
                    <key>response</key><integer>3201</integer>
                    <key>desc</key><string>wrong slot number</string>
                  </dict>
                </plist>
              ))
            }

            // good : device -> photo -> file
            val stream = File.findByKey(filename) match {
              case Full(file) => new java.io.ByteArrayInputStream(file.content.get)
              case _ => return Full(XmlResponse(
                <plist version="1.0">
                  <dict>
                    <key>response</key><integer>3204</integer>
                    <key>desc</key><string>file not found</string>
                  </dict>
                </plist>
              ))
            }
            val mime = "image/" + filename.substring(filename.lastIndexOf(".")+1)
            Full(StreamingResponse(stream,
                              () => stream.close,
                              stream.available,
                              List("Content-Type" -> mime),
                              Nil,
                              200))
          }

            // error
          case _ => Full(XmlResponse(
            <plist version="1.0">
              <dict>
                <key>response</key><integer>3203</integer>
                <key>desc</key><string>photo not found</string>
              </dict>
            </plist>
          ))
        }
      }
        // error
      case _ => Full(XmlResponse(
        <plist version="1.0">
          <dict>
            <key>response</key><integer>3200</integer>
            <key>desc</key><string>device not found</string>
          </dict>
        </plist>
      ))
    }
  }

  // get photo by filename
  def res1210_get_photo_filename(filename: String, extension: String) : Box[LiftResponse] = {

    val stream = File.findByKey(filename + "." + extension) match {
      case Full(file) => new java.io.ByteArrayInputStream(file.content.get)
      case _ => return Full(XmlResponse(
        <plist version="1.0">
          <dict>
            <key>response</key><integer>3210</integer>
            <key>desc</key><string>file not found</string>
          </dict>
        </plist>
      ))
    }
    val mime = "image/" + extension
    Full(StreamingResponse(stream,
                      () => stream.close,
                      stream.available,
                      List("Content-Type" -> mime),
                      Nil,
                      200))
  }

  // propose
  def res1300_propose(deviceId: String, destId: String) : Box[LiftResponse] = {
    Relation.create.fromId(deviceId).toId(destId).status("?").save

    // return result xml
    return Full(XmlResponse(
      <plist version="1.0">
        <dict>
          <key>response</key><integer>2300</integer>
          <key>desc</key><string>propose succeed</string>
         </dict>
      </plist>
    ))
  }

  // say yes
  def res1310_say_yes(deviceId: String, destId: String) : Box[LiftResponse] = {
    Relation.find(By(Relation.fromId, deviceId), By(Relation.toId, destId)) match {
      case Full(r) => {
        r.status("y").save
        Full(XmlResponse(
          <plist version="1.0">
            <dict>
              <key>response</key><integer>2310</integer>
              <key>desc</key><string>say yes succeed</string>
             </dict>
          </plist>
        ))
      }
      case _ => Full(XmlResponse(
        <plist version="1.0">
          <dict>
            <key>response</key><integer>3310</integer>
            <key>desc</key><string>say yes fail</string>
           </dict>
        </plist>
      ))
    }
  }

  // say no
  def res1320_say_no(deviceId: String, destId: String) : Box[LiftResponse] = {
    Relation.find(By(Relation.fromId, deviceId), By(Relation.toId, destId)) match {
      case Full(r) => {
        r.status("n").save
        Full(XmlResponse(
          <plist version="1.0">
            <dict>
              <key>response</key><integer>2320</integer>
              <key>desc</key><string>say no succeed</string>
             </dict>
          </plist>
        ))
      }
      case _ => Full(XmlResponse(
        <plist version="1.0">
          <dict>
            <key>response</key><integer>3320</integer>
            <key>desc</key><string>say no fail</string>
           </dict>
        </plist>
      ))
    }
  }


//    def addDog(request: Req): Box[LiftResponse] = {
//    var dogName = ""
//    request.xml match {
//          case Full(<dog>{parameters @ _*}</dog>) => {
//            for(parameter <- parameters){
//                parameter match {
//                    case <name>{name}</name> => dogName = name.text
//                    case _ =>
//                }
//            }
//            val dog = new Dog("2", dogName) // Normally you'd assign a unique ID
//            Log.info("Creating a dog with name: " + dog.name)
//            return Full(InMemoryResponse(dog.toXml.toString.getBytes("UTF-8"), List("Content-Type" -> "text/xml"), Nil, 200))
//        }
//        case _ => Log.error("Invalid request");
//            Log.error("Request: " + request);
//            Log.error("Request.xml: " + request.xml);Full(BadResponse())
//    }
}

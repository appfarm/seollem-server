package appfarm.snippet

import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.S._
import _root_.net.liftweb.http.SHtml._
import _root_.scala.xml._

import _root_.net.liftweb.util.Helpers._
import appfarm.model.File

/**
 * Created by IntelliJ IDEA.
 * User: zuns00
 * Date: 8/15/11
 * Time: 3:44 PM
 * To change this template use File | Settings | File Templates.
 */

class FileUpload {
  // the request-local variable that hold the file parameter
  private object theUpload extends RequestVar[Box[FileParamHolder]](Empty)

  /**
   * Bind the appropriate XHTML to the form
   */
  def upload(xhtml: Group): NodeSeq = {

    if (S.get_?) {

      bind("ul", chooseTemplate("choose", "get", xhtml),
                      "file_upload" -> fileUpload(ul => theUpload(Full(ul))))
    }
    else {
      theUpload.is match {
        case Full(FileParamHolder(_, mime, fileName, data))
          if mime.startsWith("image/") => {
          File.create.name(fileName).content(data).save
        }
        case _ => Empty
      }
      bind("ul", chooseTemplate("choose", "post", xhtml),
        "file_name" -> theUpload.is.map(v => Text(v.fileName)),
        "mime_type" -> theUpload.is.map(v => Box.legacyNullTest(v.mimeType).map(Text).openOr(Text("No mime type supplied"))), // Text(v.mimeType)),
        "length" -> theUpload.is.map(v => Text(v.file.length.toString)),
        "md5" -> theUpload.is.map(v => Text(hexEncode(md5(v.file))))
      )
    }

  }
}
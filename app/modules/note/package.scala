package modules

import java.sql.Time

import play.api.data.{Form, FormError}
import play.api.data.Forms._
import play.api.data.format.Formatter
import modules.label.Label

package object note {

  val createNoteForm: Form[NewNote] = Form {
    mapping(
      "text" -> nonEmptyText,
      "color" -> of(colorFormFormatter),


    )(NewNote.apply)(NewNote.unapply)
  }

  def colorFormFormatter: Formatter[Color.Value] = new Formatter[Color.Value] {
    def bind(key: String, data: Map[String, String]): Either[List[FormError], Color.Value] = {
      val value = data.getOrElse(key, "")
      Color
        .findByString(value)
        .map(Right(_))
        .getOrElse(Left(List(FormError(key, s"Color not found by value:'$value'"))))
    }
    def unbind(key: String, value: Color.Value): Map[String, String] = Map(key -> value.toString)
  }

  object Color extends Enumeration {
    type Color = Value
    val Red, Green, Blue, Yellow, Orange, Cyan,Pink,Purple = Value

    def findByString(value: String): Option[note.Color.Value] = {
      values.find(_.toString == value)
    }
  }

  case class NewNote(
      text: String,
      color: Color.Value,
      labelIds: Seq[Long]
  )

  case class Note(
      id: Long,
      text: String,
      color: Color.Value
  )
}

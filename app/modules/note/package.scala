package modules


import play.api.data.{Form}
import play.api.data.Forms._


package object note {

  val createNoteForm: Form[NewNote] = Form {
    mapping(
      "text" -> nonEmptyText,
      "color" -> nonEmptyText,
      "labelIds"-> seq(longNumber)

    )(NewNote.apply)(NewNote.unapply)
  }
  val NoteForm: Form[EditNote]= Form {
    mapping(

      "text" -> nonEmptyText,
      "color"-> nonEmptyText
    )(EditNote.apply)(EditNote.unapply)
  }

  case class NewNote(
      text: String,
      color: String,
      labelIds: Seq[Long]
  )

  case class Note(
      id: Long,
      text: String,
      color:String
  )
  case class EditNote(
      text: String,
      color:String
                 )
}

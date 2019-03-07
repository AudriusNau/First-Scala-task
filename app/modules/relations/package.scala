package modules


import play.api.data.Forms.{mapping}
import play.api.data.{Form}
import play.api.data.Forms._

package object relations {

  val createRelationForm: Form[NewRelation] = Form {
    mapping(
      "note_id" -> longNumber,
      "label_id"-> longNumber
    )(NewRelation.apply)(NewRelation.unapply)
  }
 case class NewRelation(
   note_id : Long,
   label_id: Long
  )
  case class Relation(
  note_id: Long,
  label_id:Long
  )
}

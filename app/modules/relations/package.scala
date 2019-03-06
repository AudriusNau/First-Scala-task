package modules


import play.api.data.Forms.{mapping}
import play.api.data.{Form}
import play.api.data.Forms._

package object relations {

  val createRelationForm: Form[NewRelation] = Form {
    mapping(
      "noteId" -> longNumber,
      "labelId"-> longNumber
    )(NewRelation.apply)(NewRelation.unapply)
  }
 case class NewRelation(
   noteId : Long,
   labelId: Long
  )
  case class Relation(
  noteId: Long,
  labelId:Long
  )
}

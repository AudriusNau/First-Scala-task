package modules

import play.api.data.Form
import play.api.data.Forms.{mapping}
import play.api.data.Forms._


package object label {

  val createLabelForm: Form[NewLabel] = Form {
    mapping(
      "label" -> nonEmptyText,
    )(NewLabel.apply)(NewLabel.unapply)
  }

  case class NewLabel
  (
    label : String
  )
  case class Label
  (
    id : Long,
    label: String
  )
}

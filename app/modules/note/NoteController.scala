package modules.note

import javax.inject.Inject
import play.api.mvc.{Action, _}
import views.html
import modules.label.LabelRepository
import scala.concurrent.ExecutionContext

class NoteController @Inject()(
                                repo: NoteRepository,
                                repLabel:LabelRepository,
                                cc: MessagesControllerComponents
)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  def list(filter: String,labelList: List[Long]): Action[AnyContent] = Action.async { implicit request =>

    for {
      notes <- repo.list( filter = "%" + filter + "%",labelList)
      label <- repLabel.list()
    } yield {
      Ok(html.note.list(notes,filter, label, labelList))
    }
  }

  def createView() = Action.async { implicit request: MessagesRequest[AnyContent] =>
    repLabel
      .list()
      .map{
        labels=>
          Ok(html.note.create(createNoteForm, labels))
      }
  }

  def create(): Action[AnyContent] = Action.async { implicit request =>
    createNoteForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          repLabel
            .list()
            .map{
              labels=>
          Ok(html.note.create(formWithErrors,labels))
            },
        newNote =>
          repo
            .create(newNote)
            .map(_ =>
              Redirect(routes.NoteController.list("",List.empty))
            )
      )
  }
}

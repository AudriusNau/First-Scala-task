package modules.note

import javax.inject.Inject
import play.api.mvc.{Action, _}
import views.html
import modules.label.{LabelComponent, LabelRepository}

import scala.concurrent.{ExecutionContext, Future}

class NoteController @Inject()(
                                repo: NoteRepository,
                                repLabel:LabelRepository,
                                cc: MessagesControllerComponents
)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  def list(): Action[AnyContent] = Action.async { implicit request =>
    repo
      .list()
      .map {
        notes =>
          Ok(html.note.list(notes))
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
              Redirect(routes.NoteController.list())
            )
      )
  }
}

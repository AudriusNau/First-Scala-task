package modules.note

import javax.inject.Inject
import play.api.mvc.{Action, _}
import views.html

import scala.concurrent.{ExecutionContext, Future}

class NoteController @Inject()(
    repo: NoteRepository,
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

  def createView() = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(html.note.create(createNoteForm))
  }

  def create(): Action[AnyContent] = Action.async { implicit request =>
    createNoteForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(Ok(html.note.create(formWithErrors))),
        newNote =>
          repo
            .create(newNote)
            .map(_ =>
              Redirect(routes.NoteController.list())
            )
      )
  }
}

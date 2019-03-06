package modules.relations

import javax.inject.Inject
import modules.note.{NoteRepository, createNoteForm}
import play.api.mvc._
import views.html

import scala.concurrent.ExecutionContext

class RelationController @Inject()(
 repo: NoteRepository,
 cc: MessagesControllerComponents)
  (implicit ec: ExecutionContext) extends MessagesAbstractController(cc){

  def list(): Action[AnyContent] = Action.async { implicit request =>
    repo
      .list()
      .map {
        relations =>
          Ok(html.note.list(relations))
      }
  }

  def createView() = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(html.note.create(createNoteForm))
  }
}

package modules.note

import javax.inject.Inject
import play.api.mvc.{Action, _}
import views.html
import modules.label.LabelRepository

import scala.concurrent.{ExecutionContext, Future}


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
  def edit(id:Long)= Action.async{implicit request =>
    val note= repo.findById(id)
    note.map { case note =>
      note match {
        case Some(c)=> Ok(html.note.edit(c,NoteForm.fill(EditNote(c.text,c.color))))
        case None => NotFound
      }
    }
  }
  def update(id:Long) = Action.async { implicit request =>

    val futNote = repo.findById(id)
    NoteForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          for {
            noteOp <- repo.findById(id)
          } yield Ok(html.note.edit(noteOp.get, formWithErrors)),
        editNote =>
          for {
            noteOp <- repo.findById(id)
            update <- repo.update(Note(id, editNote.text, editNote.color))
          } yield Redirect(routes.NoteController.list("", List.empty)))

  }
  /** Handle computer deletion. */
  def delete(id: Long) = Action.async { implicit rs =>
    for {
      _ <- repo.delete(id)
    } yield Redirect(routes.NoteController.list("", List.empty))
  }

}

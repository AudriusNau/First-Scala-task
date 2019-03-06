package modules.label

import javax.inject.Inject
import play.api.mvc._
import views.html

import scala.concurrent.{ExecutionContext, Future}

class LabelController  @Inject()(
                                  repo: LabelRepository,
                                  cc: MessagesControllerComponents
                                )(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  def list(): Action[AnyContent] = Action.async { implicit request =>
    repo
      .list()
      .map {
        labels =>
          Ok(html.label.list(labels))
      }
  }

  def createView() = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(html.label.create(createLabelForm))
  }

  def create(): Action[AnyContent] = Action.async { implicit request =>
    createLabelForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(Ok(html.label.create(formWithErrors))),
        newLabel =>
          repo
            .create(newLabel)
            .map(_ =>
              Redirect(routes.LabelController.list())
            )
      )
  }
}

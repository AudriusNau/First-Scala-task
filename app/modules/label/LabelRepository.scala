package modules.label

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class LabelRepository @Inject()(dbConfigProvider:DatabaseConfigProvider)(implicit ec:ExecutionContext){
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class LabelTable(tag:Tag) extends Table[Label](tag, "labels") {
    def id=  column[Long]("id", O.PrimaryKey, O.AutoInc)
    def label=column[String]("label")

    def * = (id,label) <> ((Label.apply _).tupled, Label.unapply)
  }
  private val labels = TableQuery[LabelTable]

  def list(): Future[Seq[Label]] = {
    db.run(labels.result)
  }

  def create(newLabel: NewLabel): Future[Label] = {
    val query = (labels.map(p =>p.label)
      returning labels
      )+= newLabel.label
    db.run(query)
  }

}

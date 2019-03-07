package modules.label

import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.{JdbcProfile, PostgresProfile}

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class LabelRepository  @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec:ExecutionContext)extends LabelComponent with HasDatabaseConfigProvider[PostgresProfile]  {

  import profile.api._

  private class LabelTable(tag:Tag) extends Table[Label](tag, "labels") {
    def id=  column[Long]("id", O.PrimaryKey, O.AutoInc)
    def label=column[String]("label")

    def * = (id,label) <> ((Label.apply _).tupled, Label.unapply)
  }


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

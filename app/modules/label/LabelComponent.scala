package modules.label
import play.api.db.slick.HasDatabaseConfig
import slick.jdbc.PostgresProfile


trait LabelComponent {self: HasDatabaseConfig[PostgresProfile] =>

  import profile.api._

   class LabelTable(tag:Tag) extends Table[Label](tag, "labels") {
    def id=  column[Long]("id", O.PrimaryKey, O.AutoInc)
    def label=column[String]("label")

    def * = (id,label) <> ((Label.apply _).tupled, Label.unapply)
  }
 val labels = TableQuery[LabelTable]

}

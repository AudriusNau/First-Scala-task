package modules.relations

import play.api.db.slick.HasDatabaseConfig
import slick.jdbc.PostgresProfile

trait RelationComponent { self: HasDatabaseConfig[PostgresProfile] =>

  import profile.api._

  class RelationTable(tag: Tag)extends Table[Relation](tag, "relation_tbl") {
    def note_id= column[Long]("note_id")
    def label_id=column[Long]("label_id")
    def * = (note_id,label_id) <> ((Relation.apply _).tupled, Relation.unapply)
    def pk = primaryKey("relationTbl_pk",(note_id,label_id))
  }

  val relations = TableQuery[RelationTable]
}

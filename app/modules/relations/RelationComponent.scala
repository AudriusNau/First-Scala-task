package modules.relations

import play.api.db.slick.HasDatabaseConfig
import slick.jdbc.PostgresProfile

trait RelationComponent { self: HasDatabaseConfig[PostgresProfile] =>

  import profile.api._

  class RelationTable(tag: Tag)extends Table[Relation](tag, "relationTbl") {
    def noteId= column[Long]("noteId")
    def labelId=column[Long]("labelId")
    def * = (noteId,labelId) <> ((Relation.apply _).tupled, Relation.unapply)
    def pk = primaryKey("relationTbl_pk",(noteId,labelId))
  }

  val relations = TableQuery[RelationTable]
}

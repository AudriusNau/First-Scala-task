package modules.note

import javax.inject.{Inject, Singleton}
import modules.relations.{Relation, RelationComponent}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.PostgresProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NoteRepository @Inject()(
                                protected val dbConfigProvider: DatabaseConfigProvider
                              )(
  implicit ec: ExecutionContext) extends RelationComponent with HasDatabaseConfigProvider[PostgresProfile]  {

  import profile.api._

  implicit private val colorColumnType = MappedColumnType.base[Color.Value, String](
    { enum => enum.toString },
    { string => Color.withName(string) }
  )

  private class NoteTable(tag: Tag) extends Table[Note](tag, "notes") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("text")
    def color = column[Color.Value]("color")

    def * = (id, name, color) <> ((Note.apply _).tupled, Note.unapply)
  }

  private val notes = TableQuery[NoteTable]
  def list(): Future[Seq[Note]] = {
    db.run(notes.result)
  }

  def create(newNote: NewNote): Future[Note] = {
    val action = ((notes.map(p => (p.name, p.color))
      returning notes
      into ((_, note) => note)
      ) += (newNote.text, newNote.color))
        .flatMap(
          note =>
            DBIO
              .sequence(newNote.labelIds.map(id => relations += Relation(note.id, id)))
              .map(_ => note)
        )
    db.run(action)
  }
}

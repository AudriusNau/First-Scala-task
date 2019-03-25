package modules.note

import javax.inject.{Inject, Singleton}
import modules.label.{Label, LabelComponent}
import modules.relations.{Relation, RelationComponent}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.PostgresProfile
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NoteRepository @Inject()(
                                protected val dbConfigProvider: DatabaseConfigProvider
                              )(
  implicit ec: ExecutionContext) extends RelationComponent with LabelComponent with HasDatabaseConfigProvider[PostgresProfile]  {

  import profile.api._



  private class NoteTable(tag: Tag) extends Table[Note](tag, "notes") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("text")
    def color = column[String]("color")

    def * = (id, name, color) <> ((Note.apply _).tupled, Note.unapply)
  }

  private val notes = TableQuery[NoteTable]
  private var asc : Boolean= true

  def list(filter: String = "%", labelsFilter: List[Long]): Future[mutable.LinkedHashMap[Note, Seq[Label]]] = {

    val query = notes
      .joinLeft(relations)
      .on { case (n, r) =>
          n.id === r.note_id
      }
      .joinLeft(labels)
      .on { case ((n, r), l) =>
          r.map(_.label_id) === l.id
      }
      .filter { case ((n, r), l) =>
        n.name.toLowerCase like filter.toLowerCase
      }
      .filter { case ((n, r), l) =>
        Option(labelsFilter)
          .filter(_.nonEmpty)
          .map { nonEmptyFilter =>
            l.map(_.id.inSet(nonEmptyFilter)).getOrElse(false: Rep[Boolean])
          }
          .getOrElse(true: Rep[Boolean])
      }
      .map { case ((n, r), l) =>
        n
      }
      .distinct
      .joinLeft(relations)
      .on { case (n, r) =>
        n.id === r.note_id
      }
      .joinLeft(labels)
      .on { case ((n, r), l) =>
        r.map(_.label_id) === l.id
      }
      .map { case ((n, r), l) =>
        (n, l)
      }
    val sortedQuery = if (asc) {
      query.sortBy(_._1.name.asc)
    }else{
      query.sortBy(_._1.name.desc)
    }
    asc = !asc


    val filteredList =  db.run(sortedQuery.result).map {
    seq => seq.foldLeft(mutable.LinkedHashMap[Note, Seq[Label]]()) {
      case (acc,(n, Some(l)))=> {
        acc += (
          n -> (l +: acc.getOrElse(n,Seq[Label]()))
        )
      }
      }

    }
      filteredList
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
  def findById(id: Long): Future[Option[Note]] =
    db.run(notes.filter(_.id === id).result.headOption)

  def update(note: Note): Future[Int] = {
    val updated = notes.insertOrUpdate(Note(note.id, note.text,note.color))
    db.run(updated)
  }
  /** Delete a computer. */
  def delete(id: Long): Future[Unit] = {

    db.run(relations.filter(_.note_id === id).delete).map(_ => ())
    db.run(notes.filter(_.id === id).delete).map(_ => ())
  }

}

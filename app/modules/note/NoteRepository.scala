package modules.note

import javax.inject.{Inject, Singleton}
import modules.label
import modules.label.{Label, LabelComponent}
import modules.relations.{Relation, RelationComponent}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.PostgresProfile

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

  def list(filter: String = "%", labelsFilter: List[Long]): Future[Seq[(Note, Seq[Label])]] = {

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

    val test = db.run(query.result).map {
      t =>
        t.groupBy(_._1).mapValues(t => t.map(what => what._2.getOrElse(null))).toSeq
    }
    test

//    for {
//      v <- test
//      s <- v.groupBy(_._1).mapValues(t => t.map(what => what._2.getOrElse(null)))
//    }yield s
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

package modules.note

import com.dimafeng.testcontainers.PostgreSQLContainer
import org.scalatest.TestData
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.Application
import utility.application.TestApplications.basicDatabaseTestApplication
import utility.database.PlayPostgreSQLTest

import scala.concurrent.{ExecutionContext, Future}

class NoteRepositorySpec extends PlaySpec
  with GuiceOneAppPerTest
  with PlayPostgreSQLTest
  with ScalaFutures
  with IntegrationPatience {

  override val container: PostgreSQLContainer = PostgreSQLContainer("postgres:alpine")

  override def newAppForTest(testData: TestData): Application = {
    basicDatabaseTestApplication(container)
  }

  "NoteRepository" must {
    "create note" in {
      val repository = app.injector.instanceOf[NoteRepository]
      val note = repository.create(NewNote("First note", "ffffff", Seq.empty)).futureValue
      note mustEqual Note(1, "First note", "ffffff")
    }

    "filter note list" in {
      val repository = app.injector.instanceOf[NoteRepository]
      implicit lazy val executionContext = app.injector.instanceOf[ExecutionContext]
      whenReady(
        Future.sequence(
          Seq(
            repository.create(NewNote("First note", "ffffff", Seq.empty)),
            repository.create(NewNote("Second note", "cccccc", Seq.empty))
          )
        )
      ) { _ =>
        val filteredList = repository.list("first note").futureValue
        filteredList must contain theSameElementsInOrderAs  Seq(Note(1, "First note", "ffffff"))
      }
    }
  }
}

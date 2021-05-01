package controllers

import org.scalatestplus.play.{ BaseOneAppPerSuite, PlaySpec }
import play.api.test.FakeRequest
import play.api.test.Helpers._

/**
 * Runs a functional test with the application, using an in memory
 * database.  Migrations are handled automatically by play-flyway
 */
class FunctionalSpec extends PlaySpec with BaseOneAppPerSuite with TestApplicationFactory {

  "App" should {
    "work with postgres Database" in {
      val future = route(
        app,
        FakeRequest(GET, "/").withHeaders(("Authorization", "GlobalAdmin"))
      ).get
      status(future) mustEqual 200
    }
  }

}

package controllers

import java.util.UUID

import models.api._
import org.scalatestplus.play.{ BaseOneAppPerSuite, PlaySpec }
import play.api.libs.json._
import play.api.test.Helpers._
import testutils.BaseFakeRequest
import testutils.BaseFakeRequest._

class ActorControllerTest extends PlaySpec with BaseOneAppPerSuite with TestApplicationFactory {

  /** —————————————————————— */
  /**  ACTORSCONTROLLER      */
  /** —————————————————————— */
  "Actor" must {
    "query not hello" in {
      val result = BaseFakeRequest(GET, "/v1/public/helloworld/test")
        .withHeader(("Authorization", "test@test.de"))
        .get

      val content = contentAsString(result)
      val parsed  = content

      parsed mustBe "the query was not 'hello'"

    }

    "throughput" in {
      for (_ <- 0 to 10) {
        BaseFakeRequest(GET, "/v1/public/helloworld/test")
          .withHeader(("Authorization", "test@test.de"))
          .get
      }
    }

    "query hello " in {
      val result = BaseFakeRequest(GET, "/v1/public/helloworld/hello")
        .withHeader(("Authorization", "test@test.de"))
        .get

      val content = contentAsString(result)
      val parsed  = content

      parsed mustBe "hello you"
    }

  }

}

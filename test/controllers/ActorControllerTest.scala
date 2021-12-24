package controllers

import java.util.UUID

import org.scalatestplus.play.{BaseOneAppPerSuite, PlaySpec}
import play.api.libs.json._
import play.api.test.Helpers._
import testutils.BaseFakeRequest
import testutils.FakeRequestUtils._

class ActorControllerTest extends PlaySpec with BaseOneAppPerSuite with TestApplicationFactory {

  /** —————————————————————— */
  /** ACTORSCONTROLLER */
  /** —————————————————————— */
  "Actor" must {
    "query hello" in {
      val result = Get("/v1/public/helloworld/test", "AuthHeader")
      val content = contentAsString(result)
      val parsed = content
      parsed mustBe "the query was not 'hello'"
    }

    "throughput" in {
      for (_ <- 0 to 10)
        Get("/v1/public/helloworld/test", "test@test.de")

    }

  }

}

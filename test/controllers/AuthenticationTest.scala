package controllers

import java.util.UUID

import com.google.inject.Inject
import com.typesafe.config.Config
import models.api._
import org.scalatestplus.play.{ BaseOneAppPerSuite, PlaySpec }
import play.api.libs.json._
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import play.api.libs.json.Reads
import common.utils.PagedGen._
import play.api.test.CSRFTokenHelper._
import testutils.BaseFakeRequest
import testutils.BaseFakeRequest._

import scala.concurrent.Future

class AuthenticationTest extends PlaySpec with BaseOneAppPerSuite with TestApplicationFactory {

  val config: Config = app.injector.instanceOf[Config]

  /** ———————————————— */
  /**  AUTHENTICATION  */
  /** ———————————————— */
  "Authentication on HealthCheck" must {
    "accept probes" in {
      status(route(app, FakeRequest(GET, "/")).get) mustEqual 200
      status(route(app, FakeRequest(GET, "/readiness")).get) mustEqual 200
      status(route(app, FakeRequest(GET, "/liveness")).get) mustEqual 200
    }
  }

  "Authentication on Company" must {
    "get me" in {

      BaseFakeRequest(GET, "/v1/companies/me").get checkStatus 401
      BaseFakeRequest(GET, "/v1/companies/me").withHeader(("Authorization", "empty@empty")).get checkStatus 404
      BaseFakeRequest(GET, "/v1/companies/me").withHeader(("Authorization", "test@test.de")).get checkStatus 200

    }

  }

}

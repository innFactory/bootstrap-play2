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

  val company1ValidEmail = "test@test.de"
  val company2ValidEmail = "test@test6.de"
  val invalidEmail       = "empty@empty.de"

  /** ———————————————— */
  /** AUTHENTICATION */
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
      BaseFakeRequest(GET, "/v1/companies/me").withHeader(("Authorization", invalidEmail)).get checkStatus 404
      BaseFakeRequest(GET, "/v1/companies/me").withHeader(("Authorization", company1ValidEmail)).get checkStatus 200
    }

    "get single" in {
      BaseFakeRequest(GET, "/v1/companies/231f5e3d-31db-4be5-9db9-92955e03507c")
        .withHeader(("Authorization", invalidEmail))
        .get checkStatus 403
      BaseFakeRequest(GET, "/v1/companies/231f5e3d-31db-4be5-9db9-92955e03507c").get checkStatus 401
    }

    "post" in {
      BaseFakeRequest(POST, "/v1/companies")
        .withHeader(("Authorization", invalidEmail))
        .withJsonBody(Json.parse(s"""
                                    |{
                                    |"firebaseUser": [
                                    |   "test5@test5.de"
                                    | ],
                                    |"settings": {
                                    |"test": "test"
                                    |}
                                    |}
                                    |""".stripMargin))
        .getWithBody checkStatus 403
    }

    "patch" in {
      BaseFakeRequest(PATCH, "/v1/companies")
        .withHeader(("Authorization", invalidEmail))
        .withJsonBody(Json.parse(s"""
                                                                          {
                                    | "id": "231f5e3d-31db-4be5-9db9-92955e03507c",
                                    | "firebaseUser": ["test@test.de"],
                                    | "settings": {"test2": "test2"}
                                    |}
                                    |""".stripMargin))
        .getWithBody checkStatus 403
    }

    "delete" in {
      BaseFakeRequest(DELETE, "/v1/companies/231f5e3d-31db-4be5-9db9-92955e03507c")
        .withHeader(("Authorization", invalidEmail))
        .get checkStatus 403
    }

  }

  "Authentication on Location" must {
    "get by id" in {
      BaseFakeRequest(GET, "/v1/locations/1").get checkStatus 401
      BaseFakeRequest(GET, "/v1/locations/1").withHeader(("Authorization", invalidEmail)).get checkStatus 403
      BaseFakeRequest(GET, "/v1/locations/2").withHeader(("Authorization", company1ValidEmail)).get checkStatus 404
      BaseFakeRequest(GET, "/v1/locations/1").withHeader(("Authorization", company1ValidEmail)).get checkStatus 200
    }

    "get by company" in {
      BaseFakeRequest(GET, "/v1/companies/231f5e3d-31db-4be5-9db9-92955e03507c/locations").get checkStatus 401
      BaseFakeRequest(GET, "/v1/companies/b492fa98-ab60-4596-ac3c-256cc4957797/locations")
        .withHeader(("Authorization", invalidEmail))
        .get checkStatus 403
      BaseFakeRequest(GET, "/v1/companies/231f5e3d-31db-4be5-9db9-92955e03507c/locations")
        .withHeader(("Authorization", company1ValidEmail))
        .get checkStatus 200
    }

    "get by distance" in {
      BaseFakeRequest(GET, "/v1/locations/distance/1000?lat=0&lon=0").get checkStatus 401
      BaseFakeRequest(GET, "/v1/locations/distance/1000?lat=0&lon=0")
        .withHeader(("Authorization", invalidEmail))
        .get checkStatus 403
      BaseFakeRequest(GET, "/v1/locations/distance/1000?lat=0&lon=0")
        .withHeader(("Authorization", company1ValidEmail))
        .get checkStatus 200
    }

    "post" in {
      BaseFakeRequest(POST, "/v1/locations")
        .withHeader(("Authorization", invalidEmail))
        .withJsonBody(Json.parse(s"""
                                    |{
                                    |  "company": "231f5e3d-31db-4be5-9db9-92955e03507c",
                                    |  "name": "test",
                                    |  "lat": 0,
                                    |  "lon": 0
                                    |  }
                                    |""".stripMargin))
        .getWithBody checkStatus 403
    }

    "patch" in {
      BaseFakeRequest(PATCH, "/v1/locations")
        .withHeader(("Authorization", invalidEmail))
        .withJsonBody(Json.parse(s"""
                                    {
                                    |  "id": 1,
                                    |  "company": "231f5e3d-31db-4be5-9db9-92955e03507c",
                                    |  "name": "test2",
                                    |  "lat": 2,
                                    |  "lon": 2
                                    |  }
                                    |""".stripMargin))
        .getWithBody checkStatus 403
    }

    "delete" in {
      BaseFakeRequest(DELETE, "/v1/locations/1")
        .withHeader(("Authorization", invalidEmail))
        .get checkStatus 403
    }

  }

}

package controllers

import java.util.UUID

import models.api._
import org.scalatestplus.play.{ BaseOneAppPerSuite, PlaySpec }
import play.api.libs.json._
import play.api.test.Helpers._
import testutils.BaseFakeRequest
import testutils.BaseFakeRequest._

class LocationsControllerTest extends PlaySpec with BaseOneAppPerSuite with TestApplicationFactory {

  /** ———————————————— */
  /**  LOCATIONS       */
  /** ———————————————— */
  "LocationsController" must {
    "get by id" in {
      val result = BaseFakeRequest(GET, "/v1/locations/1")
        .withHeader(("Authorization", "test@test.de"))
        .get
        .parseContent[Location]
      result.id.get mustBe 1
    }

    "get by company" in {
      val result =
        BaseFakeRequest(GET, "/v1/companies/231f5e3d-31db-4be5-9db9-92955e03507c/locations")
          .withHeader(("Authorization", "test@test.de"))
          .get
          .parseContent[Seq[Location]]
      result.head.id.get mustBe 1
    }

    "get by distance" in {
      val result =
        BaseFakeRequest(GET, "/v1/locations/distance/10000?lat=0&lon=0")
          .withHeader(("Authorization", "test@test.de"))
          .get
          .parseContent[Seq[Location]]
      result.length mustBe 1
      result.head.id.get mustBe 1
    }

    "get by distance none" in {
      val result =
        BaseFakeRequest(GET, "/v1/locations/distance/100?lat=40&lon=40")
          .withHeader(("Authorization", "test@test.de"))
          .get
          .parseContent[Seq[Location]]
      result.length mustBe 0
    }

    "post" in {
      val result =
        BaseFakeRequest(POST, "/v1/locations")
          .withHeader(("Authorization", "test@test.de"))
          .withJsonBody(Json.parse(s"""
                                      |{
                                      |  "company": "231f5e3d-31db-4be5-9db9-92955e03507c",
                                      |  "name": "test",
                                      |  "lat": 0,
                                      |  "lon": 0
                                      |  }
                                      |""".stripMargin))
          .getWithBody
          .parseContent[Location]
      result.name.get mustEqual "test"
    }

    "patch" in {
      val result =
        BaseFakeRequest(PATCH, "/v1/locations")
          .withHeader(("Authorization", "test@test.de"))
          .withJsonBody(Json.parse(s"""
                                    {
                                      |  "id": 2,
                                      |  "company": "231f5e3d-31db-4be5-9db9-92955e03507c",
                                      |  "name": "test2",
                                      |  "lat": 2,
                                      |  "lon": 2
                                      |  }
                                      |""".stripMargin))
          .getWithBody
          .parseContent[Location]
      result.id.get mustEqual 2
    }

    "delete" in {
      BaseFakeRequest(DELETE, "/v1/locations/2")
        .withHeader(("Authorization", "test@test.de"))
        .get checkStatus 204
      BaseFakeRequest(DELETE, "/v1/locations/2")
        .withHeader(("Authorization", "test@test.de"))
        .get checkStatus 404
    }

  }

}

package controllers

import de.innfactory.bootstrapplay2.locations.application.models.LocationResponse
import de.innfactory.bootstrapplay2.locations.domain.models.Location
import org.scalatestplus.play.{ BaseOneAppPerSuite, PlaySpec }
import play.api.libs.json._
import play.api.test.Helpers._
import testutils.AuthUtils
import testutils.FakeRequestUtils._

class LocationsControllerTest extends PlaySpec with BaseOneAppPerSuite with TestApplicationFactory {

  private val authUtils = app.injector.instanceOf[AuthUtils]

  /** ———————————————— */
  /** LOCATIONS */
  /** ———————————————— */
  "LocationsController" must {
    "get by id" in {
      val result = Get("/v1/locations/1", authUtils.CompanyAdminEmailToken)
      val body   = contentAsJson(result).as[LocationResponse]
      body.id mustBe 1
    }

    "get by company" in {
      val result =
        Get("/v1/companies/1/locations", authUtils.CompanyAdminEmailToken)
      val body   = contentAsJson(result).as[Seq[LocationResponse]]
    }

    "post" in {
      val result =
        Post(
          "/v1/locations",
          Json.parse(s"""
                        |{
                        |  "company": 1,
                        |  "name": "test"
                        |  }
                        |""".stripMargin),
          authUtils.CompanyAdminEmailToken
        )
      println(contentAsJson(result))
      val body   = contentAsJson(result).as[LocationResponse]
    }

    "patch" in {
      val result =
        Patch(
          "/v1/locations",
          Json.parse(s"""
                       {
                        |  "id": 2,
                        |  "company": 1,
                        |  "name": "test2"
                        |  }
                        |""".stripMargin),
          authUtils.CompanyAdminEmailToken
        )
      val body   = contentAsJson(result).as[LocationResponse]
    }

    "delete" in {
      Delete("/v1/locations/2", authUtils.CompanyAdminEmailToken).getStatus mustBe 204
      Delete("/v1/locations/2", authUtils.CompanyAdminEmailToken).getStatus mustBe 404
    }

  }

}

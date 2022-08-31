package controllers

import de.innfactory.bootstrapplay2.api.LocationResponse
import org.scalatestplus.play.{BaseOneAppPerSuite, PlaySpec}
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
      val result = Get("/v1/locations/592c5187-cb85-4b66-b0fc-293989923e1e", authUtils.CompanyAdminEmailToken)
      val id = contentAsJson(result) \ "id"
      id mustBe JsDefined(JsString("592c5187-cb85-4b66-b0fc-293989923e1e"))
    }

    "get by company" in {
      val result =
        Get("/v1/companies/0ce84627-9a66-46bf-9a1d-4f38b82a38e3/locations", authUtils.CompanyAdminEmailToken)
      contentAsJson(result)
    }

    "post" in {
      val result =
        Post(
          "/v1/locations",
          Json.parse(s"""
                        |{
                        |  "company": "0ce84627-9a66-46bf-9a1d-4f38b82a38e3",
                        |  "name": "test"
                        |  }
                        |""".stripMargin),
          authUtils.CompanyAdminEmailToken
        )
      contentAsJson(result)
    }

    "patch" in {
      val result =
        Patch(
          "/v1/locations",
          Json.parse(s"""
                       {
                        |  "id": "592c5187-cb85-4b66-b0fc-293989923e1e",
                        |  "company": "0ce84627-9a66-46bf-9a1d-4f38b82a38e3",
                        |  "name": "test2"
                        |  }
                        |""".stripMargin),
          authUtils.CompanyAdminEmailToken
        )
      contentAsJson(result)
    }

    "delete" in {
      Delete(
        "/v1/locations/592c5187-cb85-4b66-b0fc-293989923e1e",
        authUtils.CompanyAdminEmailToken
      ).getStatus mustBe 204
      Delete(
        "/v1/locations/592c5187-cb85-4b66-b0fc-293989923e1e",
        authUtils.CompanyAdminEmailToken
      ).getStatus mustBe 404
    }
  }
}

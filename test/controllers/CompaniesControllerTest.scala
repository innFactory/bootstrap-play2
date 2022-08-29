package controllers

import de.innfactory.bootstrapplay2.apidefinition.CompanyResponse
import org.scalatestplus.play.{BaseOneAppPerSuite, PlaySpec}
import play.api.libs.json._
import play.api.test.Helpers._
import testutils.AuthUtils
import testutils.FakeRequestUtils._

class CompaniesControllerTest extends PlaySpec with BaseOneAppPerSuite with TestApplicationFactory {
  private val authUtils = app.injector.instanceOf[AuthUtils]

  /** ———————————————— */
  /** COMPANIES */
  /** ———————————————— */
  "CompaniesController" must {
    "get by id" in {
      val result = Get("/v1/companies/0ce84627-9a66-46bf-9a1d-4f38b82a38e3", authUtils.CompanyAdminEmailToken)

      contentAsJson(result)
    }

    "get single" in {
      val result =
        Get("/v1/companies/0ce84627-9a66-46bf-9a1d-4f38b82a38e3", authUtils.CompanyAdminEmailToken)
      contentAsJson(result)
    }

    "post" in {
      val result =
        Post(
          "/v1/companies",
          Json.parse(s"""
                        |{
                        |"settings": {
                        |   "test": "test"
                        | },
                        |"stringAttribute1": "test",
                        |"stringAttribute2": "test",
                        |"longAttribute1": 1,
                        |"booleanAttribute": true
                        |}
                        |""".stripMargin),
          authUtils.NotVerifiedEmailToken
        )
      contentAsJson(result)
    }

    "patch" in {
      val result =
        Patch(
          "/v1/companies",
          Json.parse(s"""
                         {
                        |"id": "0ce84627-9a66-46bf-9a1d-4f38b82a38e3",
                        |"settings": {
                        |   "test": "test"
                        | },
                        |"stringAttribute1": "test",
                        |"stringAttribute2": "test",
                        |"longAttribute1": 1,
                        |"booleanAttribute": true
                        |}
                        |""".stripMargin),
          authUtils.CompanyAdminEmailToken
        )
      result.getStatus mustBe 204
    }

    "delete" in {
      Delete(
        "/v1/companies/7059f786-4633-4ace-a412-2f2e90556f08",
        authUtils.CompanyAdminEmailToken
      ).getStatus mustBe 204
      Delete(
        "/v1/companies/7059f786-4633-4ace-a412-2f2e90556f08",
        authUtils.CompanyAdminEmailToken
      ).getStatus mustBe 404
    }

  }

}

package controllers

import de.innfactory.bootstrapplay2.companies.application.models.{CompanyRequest, CompanyResponse}
import de.innfactory.bootstrapplay2.companies.domain.models.Company

import java.util.UUID
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
      val result = Get("/v1/companies/1", authUtils.CompanyAdminEmailToken)

      val body = contentAsJson(result).as[CompanyResponse]
    }

    "get single" in {
      val result =
        Get("/v1/companies/1", authUtils.CompanyAdminEmailToken)
      val body = contentAsJson(result).as[CompanyResponse]
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
      val body = contentAsJson(result).as[CompanyResponse]
    }

    "patch" in {
      val result =
        Patch(
          "/v1/companies",
          Json.parse(s"""
                         {
                        |"id": 1,
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
      Delete("/v1/companies/2", authUtils.CompanyAdminEmailToken).getStatus mustBe 204
      Delete("/v1/companies/2", authUtils.CompanyAdminEmailToken).getStatus mustBe 404
    }

  }

}

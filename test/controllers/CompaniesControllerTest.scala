package controllers

import de.innfactory.bootstrapplay2.api.{CompanyAPIControllerGen, CompanyRequestBody}
import de.innfactory.smithy4play.client.GenericAPIClient.EnhancedGenericAPIClient
import org.scalatestplus.play.{BaseOneAppPerSuite, PlaySpec}
import testutils.FakeRequestClient
import de.innfactory.smithy4play.client.SmithyPlayTestUtils._

class CompaniesControllerTest extends PlaySpec with BaseOneAppPerSuite with TestApplicationFactory {
  private val companyAdminCompanyClient = CompanyAPIControllerGen.withClientAndHeaders(
    new FakeRequestClient(),
    Some(Map("Authorization" -> Seq(authUtils.CompanyAdminEmailToken)))
  )

  /** ———————————————— */
  /** COMPANIES */
  /** ———————————————— */
  "CompaniesController" must {
    "get by id" in {
      val result =
        companyAdminCompanyClient.getCompanyById("0ce84627-9a66-46bf-9a1d-4f38b82a38e3").awaitRight
      result.statusCode mustBe result.expectedStatusCode
    }

    "get all" in {
      val result =
        companyAdminCompanyClient.getAllCompanies().awaitRight
      result.statusCode mustBe result.expectedStatusCode
    }

    "post" in {
      val result =
        companyAdminCompanyClient
          .createCompany(
            CompanyRequestBody(
              stringAttribute1 = Some("test"),
              stringAttribute2 = Some("test"),
              longAttribute1 = Some(1),
              booleanAttribute = Some(true)
            )
          )
          .awaitRight
      result.statusCode mustBe result.expectedStatusCode
    }

    "patch" in {
      val result =
        companyAdminCompanyClient
          .updateCompany(
            CompanyRequestBody(
              id = Some(de.innfactory.bootstrapplay2.api.CompanyId("0ce84627-9a66-46bf-9a1d-4f38b82a38e3")),
              stringAttribute1 = Some("test2"),
              stringAttribute2 = Some("test2"),
              longAttribute1 = Some(2),
              booleanAttribute = Some(false)
            )
          )
          .awaitRight
      result.statusCode mustBe result.expectedStatusCode
    }

    "delete" in {
      val successfulDelete =
        companyAdminCompanyClient
          .deleteCompany(
            de.innfactory.bootstrapplay2.api.CompanyId("7059f786-4633-4ace-a412-2f2e90556f08")
          )
          .awaitRight
      successfulDelete.statusCode mustBe successfulDelete.expectedStatusCode
      val notFoundAfterDelete =
        companyAdminCompanyClient
          .deleteCompany(
            de.innfactory.bootstrapplay2.api.CompanyId("7059f786-4633-4ace-a412-2f2e90556f08")
          )
          .awaitLeft
      notFoundAfterDelete.statusCode mustBe 404
    }

  }

}

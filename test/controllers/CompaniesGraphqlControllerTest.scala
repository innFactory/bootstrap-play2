package controllers

import de.innfactory.bootstrapplay2.models.api._
import org.scalatestplus.play.{ BaseOneAppPerSuite, PlaySpec }
import play.api.libs.json._
import play.api.test.Helpers._
import testutils.BaseFakeRequest
import testutils.BaseFakeRequest._
import testutils.grapqhl.CompanyRequests
import testutils.grapqhl.FakeGraphQLRequest.{ getFake, routeResult }

import java.util.UUID

class CompaniesGraphqlControllerTest extends PlaySpec with BaseOneAppPerSuite with TestApplicationFactory {

  /** ———————————————— */
  /** COMPANIES */
  /** ———————————————— */
  "CompaniesController" must {

    "getAll" in {
      val fake    =
        routeResult(
          getFake(
            CompanyRequests.CompanyRequest
              .getRequest(filter = None)
          )
        )
      val content = contentAsJson(fake)
      status(fake) mustBe 200
      val parsed  = content.as[CompanyRequests.CompanyRequest.CompanyRequestResult]
      parsed.data.allCompanies.length mustBe 2

    }

    "getAll with boolean Filter" in {
      val fake    =
        routeResult(
          getFake(
            CompanyRequests.CompanyRequest
              .getRequest(filter = Some("booleanAttributeEquals=true"))
          )
        )
      val content = contentAsJson(fake)
      status(fake) mustBe 200
      val parsed  = content.as[CompanyRequests.CompanyRequest.CompanyRequestResult]
      parsed.data.allCompanies.length mustBe 1

    }

  }

}

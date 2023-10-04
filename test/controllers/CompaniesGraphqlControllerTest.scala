package controllers

import org.scalatestplus.play.{BaseOneAppPerSuite, PlaySpec}
import play.api.libs.json._
import play.api.test.Helpers._
import testutils.BaseFakeRequest
import testutils.BaseFakeRequest._
import testutils.grapqhl.CompanyRequests
import testutils.grapqhl.FakeGraphQLRequest.{getFake, routeResult}

import java.util.UUID

class CompaniesGraphqlControllerTest extends PlaySpec with BaseOneAppPerSuite with TestApplicationFactory {

  /** ———————————————— */
  /** COMPANIES */
  /** ———————————————— */
  "CompaniesController" must {

    "getAll" in {
      val fake =
        routeResult(
          getFake(
            CompanyRequests.CompanyRequest
              .getRequest(filter = None)
          )
        )
      status(fake) mustBe 200
    }

    "getAll with boolean Filter" in {
      val fake =
        routeResult(
          getFake(
            CompanyRequests.CompanyRequest
              .getRequest(filter = Some("booleanAttributeEquals=true"))
          )
        )
      status(fake) mustBe 200
    }

  }

}

package controllers

import de.innfactory.bootstrapplay2.api.{LocationAPIControllerGen, LocationRequestBody}
import de.innfactory.smithy4play.client.GenericAPIClient.EnhancedGenericAPIClient
import org.scalatestplus.play.{BaseOneAppPerSuite, PlaySpec}
import de.innfactory.smithy4play.client.SmithyPlayTestUtils._
import testutils.FakeRequestClient

class LocationsControllerTest extends PlaySpec with BaseOneAppPerSuite with TestApplicationFactory {
  private val companyAdminLocationClient = LocationAPIControllerGen.withClientAndHeaders(
    new FakeRequestClient(),
    Some(Map("Authorization" -> Seq(authUtils.CompanyAdminEmailToken)))
  )

  /** ———————————————— */
  /** LOCATIONS */
  /** ———————————————— */
  "LocationsController" must {
    "get by id" in {
      val result =
        companyAdminLocationClient.getLocationById("592c5187-cb85-4b66-b0fc-293989923e1e").awaitRight
      result.statusCode mustBe result.expectedStatusCode
    }

    "get by company" in {
      val result =
        companyAdminLocationClient.getAllLocationsByCompany("0ce84627-9a66-46bf-9a1d-4f38b82a38e3").awaitRight
      result.statusCode mustBe result.expectedStatusCode
    }

    "get all" in {
      val result = companyAdminLocationClient.getAllLocations().awaitRight
      result.statusCode mustBe result.expectedStatusCode
    }

    "post" in {
      val result = companyAdminLocationClient
        .createLocation(
          LocationRequestBody(
            company = "0ce84627-9a66-46bf-9a1d-4f38b82a38e3",
            name = Some("test")
          )
        )
        .awaitRight
      result.statusCode mustBe result.expectedStatusCode
    }

    "patch" in {
      val result = companyAdminLocationClient
        .updateLocation(
          LocationRequestBody(
            id = Some(de.innfactory.bootstrapplay2.api.LocationId("592c5187-cb85-4b66-b0fc-293989923e1e")),
            company = "0ce84627-9a66-46bf-9a1d-4f38b82a38e3",
            name = Some("test2")
          )
        )
        .awaitRight
      result.statusCode mustBe result.expectedStatusCode
    }

    "delete" in {
      val successfulDelete = companyAdminLocationClient
        .deleteLocation(
          de.innfactory.bootstrapplay2.api.LocationId("592c5187-cb85-4b66-b0fc-293989923e1e")
        )
        .awaitRight
      successfulDelete.statusCode mustBe successfulDelete.expectedStatusCode
      val deletedAfterDelete = companyAdminLocationClient
        .deleteLocation(
          de.innfactory.bootstrapplay2.api.LocationId("592c5187-cb85-4b66-b0fc-293989923e1e")
        )
        .awaitLeft
      deletedAfterDelete.statusCode mustBe 404
    }
  }
}

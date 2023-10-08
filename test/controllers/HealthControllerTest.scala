package controllers

import de.innfactory.bootstrapplay2.api.HealthAPIControllerGen
import de.innfactory.smithy4play.client.GenericAPIClient.EnhancedGenericAPIClient
import org.scalatestplus.play.{BaseOneAppPerSuite, PlaySpec}
import testutils.FakeRequestClient
import de.innfactory.smithy4play.client.SmithyPlayTestUtils._

class HealthControllerTest extends PlaySpec with BaseOneAppPerSuite with TestApplicationFactory {
  private val publicClient = HealthAPIControllerGen.withClient(new FakeRequestClient())

  /** ————————————————— */
  /** HEALTH CONTROLLER */
  /** ————————————————— */
  "HealthController" should {
    "accept GET request on base path" in {
      val result = publicClient.ping().run(None).awaitRight
      result.statusCode mustBe result.expectedStatusCode
    }
    "accept GET request on liveness check path" in {
      val result = publicClient.liveness().run(None).awaitRight
      result.statusCode mustBe result.expectedStatusCode
    }
    "accept GET request on readiness check path" in {
      val result = publicClient.readiness().run(None).awaitRight
      result.statusCode mustBe result.expectedStatusCode
    }
  }
}

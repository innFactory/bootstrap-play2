package controllers

import de.innfactory.bootstrapplay2.api.{ActorShardingAPIControllerGen, ActorSystemAPIControllerGen}
import de.innfactory.smithy4play.client.GenericAPIClient.EnhancedGenericAPIClient

import org.scalatestplus.play.{BaseOneAppPerSuite, PlaySpec}
import testutils.FakeRequestClient
import de.innfactory.smithy4play.client.SmithyPlayTestUtils._

import java.nio.charset.StandardCharsets

class ActorControllerTest extends PlaySpec with BaseOneAppPerSuite with TestApplicationFactory {
  private val actorSystemClient =
    ActorSystemAPIControllerGen.withClientAndHeaders(new FakeRequestClient(), Some(Map("Authorization" -> Seq("key"))))
  private val actorShardingClient =
    ActorShardingAPIControllerGen.withClientAndHeaders(
      new FakeRequestClient(),
      Some(Map("Authorization" -> Seq("key")))
    )

  /** —————————————————————— */
  /** ACTORSCONTROLLER */
  /** —————————————————————— */
  "ActorSystem" must {
    "query invalid message" in {
      val result = actorSystemClient.helloworldViaSystem("test").awaitLeft
      result.statusCode mustBe 400
      val error = new String(result.error, StandardCharsets.UTF_8)
      error mustBe "{\"message\":\"the query was not 'hello'\"}"
    }

    "query hello" in {
      val result = actorSystemClient.helloworldViaSystem("hello").awaitRight
      result.statusCode mustBe result.expectedStatusCode
    }

    "throughput" in {
      for (_ <- 0 to 10) {
        val result = actorSystemClient.helloworldViaSystem("hello").awaitRight
        result.statusCode mustBe result.expectedStatusCode
      }
    }
  }

  "ActorSharding" must {
    "query invalid message" in {
      val result = actorShardingClient.helloworldViaSharding("test").awaitLeft
      result.statusCode mustBe 400
      val error = new String(result.error, StandardCharsets.UTF_8)
      error mustBe "{\"message\":\"the query was not 'hello'\"}"
    }

    "query hello" in {
      val result = actorShardingClient.helloworldViaSharding("hello").awaitRight
      result.statusCode mustBe result.expectedStatusCode
    }

    "throughput" in {
      for (_ <- 0 to 10) {
        val result = actorShardingClient.helloworldViaSharding("hello").awaitRight
        result.statusCode mustBe result.expectedStatusCode
      }
    }
  }

}

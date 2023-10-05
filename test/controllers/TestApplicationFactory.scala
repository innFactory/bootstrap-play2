package controllers

import akka.stream.Materializer
import com.google.inject.Inject
import org.scalatestplus.play.FakeApplicationFactory
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.{Binding, Module}
import play.api.{Application, Configuration, Environment}
import de.innfactory.play.flyway.test.TestFlywayMigrator
import de.innfactory.smithy4play.client.{RequestClient, SmithyClientResponse}
import io.opentelemetry.api.GlobalOpenTelemetry
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import testutils.{AuthUtils, DateTimeUtil}

import scala.concurrent.ExecutionContext

/**
 * Set up an application factory that runs flyways migrations on in memory database.
 */
trait TestApplicationFactory extends FakeApplicationFactory {
  implicit var ec: ExecutionContext = _
  implicit var mat: Materializer = _
  var authUtils: AuthUtils = _

  GlobalOpenTelemetry.resetForTest()
  DateTimeUtil.setToDateTime("2022-03-07T00:00:00.001Z")

  def fakeApplication(): Application = {
    val app = GuiceApplicationBuilder()
      .bindings(new FlywayModule)
      .build()

    ec = app.injector.instanceOf[ExecutionContext]
    mat = app.injector.instanceOf[Materializer]
    authUtils = app.injector.instanceOf[AuthUtils]

    app
  }
}

class FlywayModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] =
    Seq(bind[FlywayMigrator].toSelf.eagerly())
}

class FlywayMigrator @Inject() (env: Environment, configuration: Configuration)
    extends TestFlywayMigrator(configuration, env)

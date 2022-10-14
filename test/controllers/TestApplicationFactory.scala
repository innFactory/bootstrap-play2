package controllers

import com.google.inject.Inject
import org.scalatestplus.play.FakeApplicationFactory
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.{Binding, Module}
import play.api.{Application, Configuration, Environment}
import de.innfactory.play.flyway.test.TestFlywayMigrator
import io.opentelemetry.api.GlobalOpenTelemetry

/**
 * Set up an application factory that runs flyways migrations on in memory database.
 */
trait TestApplicationFactory extends FakeApplicationFactory {
  GlobalOpenTelemetry.resetForTest()
  GlobalOpenTelemetry.set(null)

  def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .bindings(new FlywayModule)
      .build()
}

class FlywayModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] =
    Seq(bind[FlywayMigrator].toSelf.eagerly())
}

class FlywayMigrator @Inject() (env: Environment, configuration: Configuration)
    extends TestFlywayMigrator(configuration, env)

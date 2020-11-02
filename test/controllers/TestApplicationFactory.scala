package controllers

import java.util.Properties

import com.google.inject.Inject
import org.flywaydb.core.Flyway
import org.flywaydb.core.internal.jdbc.DriverDataSource
import org.scalatestplus.play.FakeApplicationFactory
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.{ Binding, Module }
import play.api.{ Application, Configuration, Environment, Logger }
import de.innfactory.play.flyway.test.TestFlywayMigrator

/**
 * Set up an application factory that runs flyways migrations on in memory database.
 */
trait TestApplicationFactory extends FakeApplicationFactory {
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

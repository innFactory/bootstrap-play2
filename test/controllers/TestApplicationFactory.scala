package controllers

import java.util.Properties

import com.google.inject.Inject
import org.flywaydb.core.Flyway
import org.flywaydb.core.internal.util.jdbc.DriverDataSource
import org.scalatestplus.play.FakeApplicationFactory
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.{Binding, Module}
import play.api.{Application, Configuration, Environment, Logger}

/**
 * Set up an application factory that runs flyways migrations on in memory database.
 */
trait TestApplicationFactory extends FakeApplicationFactory {
  def fakeApplication(): Application = {
    new GuiceApplicationBuilder()
      .bindings(new FlywayModule)
      .build()
  }
}

class FlywayModule extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    Seq(bind[FlywayMigrator].toSelf.eagerly() )
  }
}

class FlywayMigrator @Inject()(env: Environment, configuration: Configuration) {
  def onStart(): Unit = {
    Logger.info("Creating Flyway context")
    val driver = configuration.get[String]("bootstrapplay2test.database.driver")
    val url = configuration.get[String]("bootstrapplay2test.database.testUrl")
    val user = configuration.get[String]("bootstrapplay2test.database.testUser")
    val password =  configuration.get[String]("bootstrapplay2test.database.testPassword")
    val flyway = new Flyway
    flyway.setDataSource(new DriverDataSource(env.classLoader, driver, url, user, password, new Properties()))
    flyway.setLocations("filesystem:test/resources/migration")
    Logger.info("Cleaning Flyway Test Database")
    flyway.clean()
    Logger.info("Flyway/Migrate")
    flyway.migrate()
    flyway.info().all().map(a => Logger.info(a.getChecksum.toString.concat(" ".concat(a.getDescription))).toString)
    Logger.info("MIGRATION FINISHED")

  }
  onStart()
}

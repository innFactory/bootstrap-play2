package controllers

import java.util.Properties

import com.google.inject.Inject
import org.flywaydb.core.Flyway
import org.flywaydb.core.internal.jdbc.DriverDataSource
import org.scalatestplus.play.FakeApplicationFactory
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.{ Binding, Module }
import play.api.{ Application, Configuration, Environment, Logger }

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

class FlywayMigrator @Inject()(env: Environment, configuration: Configuration) {
  val logger = Logger("application")
  def onStart(): Unit = {

    logger.info("Creating Flyway context")
    val driver = configuration.get[String]("test.database.driver")
    val url    = configuration.get[String]("test.database.testUrl")
    val user   = configuration.get[String]("test.database.testUser")
    val password =
      configuration.get[String]("test.database.testPassword")
    import org.flywaydb.core.Flyway

    val flyway: Flyway = Flyway.configure
      .dataSource(new DriverDataSource(env.classLoader, driver, url, user, password, new Properties()))
      .schemas("postgis")
      .baselineOnMigrate(true)
      .locations("filesystem:conf/db/migration", "filesystem:test/resources/migration")
      .load
    logger.info("Cleaning Flyway Test Database")
    flyway.clean()
    logger.info("Flyway/Migrate")
    flyway.migrate()
    logger.info("MIGRATION FINISHED")
    logger.info(flyway.info().toString)
  }
  onStart()
}

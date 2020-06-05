import java.util.Properties

import javax.inject.{ Inject, Provider, Singleton }
import com.typesafe.config.Config
import firebaseAuth.{ FirebaseJWTValidator, JwtValidator, MockJWTValidator }
import play.api.inject.ApplicationLifecycle
import play.api.{ Configuration, Environment, Logger, Mode }
import slick.jdbc.JdbcBackend.Database
import com.google.inject.AbstractModule
import db.{ CompaniesDAO, LocationsDAO, SlickCompaniesDAO, SlickLocationsDAO }
import org.flywaydb.core.internal.jdbc.DriverDataSource
import play.api.libs.concurrent.AkkaGuiceSupport
import play.api.mvc.AnyContent
import repositories.{ CompaniesRepository, CompaniesRepositoryImpl, LocationRepository, LocationRepositoryImpl }

import scala.concurrent.Future

/**
 * This module handles the bindings for the API to the Slick implementation.
 *
 * https://www.playframework.com/documentation/latest/ScalaDependencyInjection#Programmatic-bindings
 */
class Module(environment: Environment, configuration: Configuration) extends AbstractModule with AkkaGuiceSupport {

  val logger = Logger("application")

  override def configure(): Unit = {
    logger.info(s"Configuring ${environment.mode}")
    bind(classOf[Database]).toProvider(classOf[DatabaseProvider])
    bind(classOf[firebaseCreationService]).asEagerSingleton()
    bind(classOf[firebaseDeletionService]).asEagerSingleton()
    bind(classOf[FlywayMigrator]).asEagerSingleton()
    bind(classOf[LocationsDAO]).to(classOf[SlickLocationsDAO])
    bind(classOf[LocationRepository]).to(classOf[LocationRepositoryImpl])
    bind(classOf[CompaniesRepository]).to(classOf[CompaniesRepositoryImpl])
    bind(classOf[LocationsDAOCloseHook]).asEagerSingleton()
    bind(classOf[CompaniesDAO]).to(classOf[SlickCompaniesDAO])
    bind(classOf[CompaniesDAOCloseHook]).asEagerSingleton()

    /**
     * Inject Modules Influx etc. depended on environment (Test, Prod, Dev)
     */
    if (environment.mode == Mode.Test) {
      logger.info(s"- - - Binding Services for for Test Mode - - -")
      bind(classOf[JwtValidator])
        .to(classOf[MockJWTValidator])
    } else {
      logger.info(s"- - - Binding Services for for Prod/Dev Mode - - -")
      bind(classOf[JwtValidator])
        .to(classOf[FirebaseJWTValidator])
    }

  }

}

/** Migrate Flyway on application start */
class FlywayMigrator @Inject()(env: Environment, configuration: Configuration) {
  val logger = Logger("application")
  logger.info("Creating Flyway context")
  val driver = configuration.get[String]("bootstrap-play2.database.driver")
  val url    = configuration.get[String]("bootstrap-play2.database.url")
  val user   = configuration.get[String]("bootstrap-play2.database.user")
  val password =
    configuration.get[String]("bootstrap-play2.database.password")

  import org.flywaydb.core.Flyway

  val flyway: Flyway = Flyway.configure
    .dataSource(new DriverDataSource(env.classLoader, driver, url, user, password, new Properties()))
    .schemas("postgis")
    .baselineOnMigrate(true)
    .locations("filesystem:conf/db/migration")
    .load
  logger.info("Flyway is migrating the database to the newest version")
  flyway.migrate()
  logger.info("Database migration complete")
}

/** Creates FirebaseApp on Application creation */
class firebaseCreationService @Inject()(config: Config) {
  FirebaseJWTValidator.instanciateFirebase(config.getString("firebase.file"), "")
}

/** Deletes FirebaseApp safely. Important on dev restart. */
class firebaseDeletionService @Inject()(lifecycle: ApplicationLifecycle) {
  lifecycle.addStopHook { () =>
    Future.successful(FirebaseJWTValidator.deleteFirebase())
  }
}

@Singleton
class DatabaseProvider @Inject()(config: Config) extends Provider[Database] {
  lazy val get = Database.forConfig("bootstrap-play2.database", config)
}

/** Closes DAO. Important on dev restart. */
class CompaniesDAOCloseHook @Inject()(dao: CompaniesDAO, lifecycle: ApplicationLifecycle) {
  lifecycle.addStopHook { () =>
    Future.successful(dao.close())
  }
}

/** Closes DAO. Important on dev restart. */
class LocationsDAOCloseHook @Inject()(dao: LocationsDAO, lifecycle: ApplicationLifecycle) {
  lifecycle.addStopHook { () =>
    Future.successful(dao.close())
  }
}

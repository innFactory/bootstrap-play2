import java.util.Properties

import javax.inject.{ Inject, Provider, Singleton }
import com.typesafe.config.Config
import play.api.inject.ApplicationLifecycle
import play.api.{ Configuration, Environment, Logger, Mode }
import slick.jdbc.JdbcBackend.Database
import com.google.inject.AbstractModule
import db.{ CompaniesDAO, LocationsDAO, SlickCompaniesSlickDAO, SlickLocationsDAO }
import de.innfactory.auth.firebase.FirebaseBase
import de.innfactory.auth.firebase.validator.{ JWTValidatorMock, JwtValidator, JwtValidatorImpl }
import de.innfactory.play.flyway.FlywayMigrator
import play.api.libs.concurrent.AkkaGuiceSupport
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
    bind(classOf[FlywayMigratorImpl]).asEagerSingleton()
    bind(classOf[LocationsDAO]).to(classOf[SlickLocationsDAO])
    bind(classOf[LocationRepository]).to(classOf[LocationRepositoryImpl])
    bind(classOf[CompaniesRepository]).to(classOf[CompaniesRepositoryImpl])
    bind(classOf[LocationsDAOCloseHook]).asEagerSingleton()
    bind(classOf[CompaniesDAO]).to(classOf[SlickCompaniesSlickDAO])
    bind(classOf[CompaniesDAOCloseHook]).asEagerSingleton()

    /**
     * Inject Modules depended on environment (Test, Prod, Dev)
     */
    if (environment.mode == Mode.Test) {
      logger.info(s"- - - Binding Services for for Test Mode - - -")
      bind(classOf[JwtValidator])
        .to(classOf[JWTValidatorMock]) // Bind Mock JWT Validator for Test Mode
    } else {
      logger.info(s"- - - Binding Services for for Prod/Dev Mode - - -")
      bind(classOf[JwtValidator])
        .to(classOf[JwtValidatorImpl]) // Bind Prod JWT Validator for Prod/Dev Mode
    }

  }

}

/** Migrate Flyway on application start */
class FlywayMigratorImpl @Inject() (env: Environment, configuration: Configuration)
    extends FlywayMigrator(configuration, env, configIdentifier = "bootstrap-play2")

/** Creates FirebaseApp on Application creation */
class firebaseCreationService @Inject() (config: Config, env: Environment) {
  if (env.mode == Mode.Prod || env.mode == Mode.Dev) {
    FirebaseBase.instantiateFirebase(config.getString("firebase.file"))
  }
}

/** Deletes FirebaseApp safely. Important on dev restart. */
class firebaseDeletionService @Inject() (lifecycle: ApplicationLifecycle) {
  lifecycle.addStopHook { () =>
    Future.successful(FirebaseBase.deleteFirebase())
  }
}

@Singleton
class DatabaseProvider @Inject() (config: Config) extends Provider[Database] {
  lazy val get = Database.forConfig("bootstrap-play2.database", config)
}

/** Closes DAO. Important on dev restart. */
class CompaniesDAOCloseHook @Inject() (dao: CompaniesDAO, lifecycle: ApplicationLifecycle) {
  lifecycle.addStopHook { () =>
    Future.successful(dao.close())
  }
}

/** Closes DAO. Important on dev restart. */
class LocationsDAOCloseHook @Inject() (dao: LocationsDAO, lifecycle: ApplicationLifecycle) {
  lifecycle.addStopHook { () =>
    Future.successful(dao.close())
  }
}

import com.google.auth.Credentials
import com.google.auth.oauth2.GoogleCredentials

import java.util.Properties
import javax.inject.{ Inject, Provider, Singleton }
import com.typesafe.config.Config
import play.api.inject.ApplicationLifecycle
import play.api.{ Configuration, Environment, Logger, Mode }
import slick.jdbc.JdbcBackend.Database
import com.google.inject.AbstractModule
import de.innfactory.auth.firebase.FirebaseBase
import de.innfactory.auth.firebase.FirebaseBase.getClass
import de.innfactory.auth.firebase.validator.{ JWTValidatorMock, JwtValidator, JwtValidatorImpl }
import de.innfactory.bootstrapplay2.db.{ CompaniesDAO, LocationsDAO }
import de.innfactory.play.flyway.FlywayMigrator
import io.opencensus.exporter.trace.jaeger.{ JaegerExporterConfiguration, JaegerTraceExporter }
import io.opencensus.exporter.trace.logging.LoggingTraceExporter
import io.opencensus.exporter.trace.stackdriver.{ StackdriverTraceConfiguration, StackdriverTraceExporter }
import io.opencensus.trace.AttributeValue
import play.api.libs.concurrent.AkkaGuiceSupport

import java.io.InputStream
import scala.concurrent.Future
import scala.jdk.CollectionConverters.MapHasAsJava

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
    bind(classOf[FlywayMigratorImpl]).asEagerSingleton()
    bind(classOf[DAOCloseHook]).asEagerSingleton()

    /**
     * Inject Modules depended on environment (Test, Prod, Dev)
     */
    if (environment.mode == Mode.Test) {

      logger.info(s"- - - Binding Services for for Test Mode - - -")

      // Bind Mock JWT Validator for Test Mode
      bind(classOf[JwtValidator]).to(classOf[JWTValidatorMock])

    } else if (environment.mode == Mode.Dev) {

      logger.info(s"- - - Binding Services for for Dev Mode - - -")

      // Firebase
      bind(classOf[firebaseCreationService]).asEagerSingleton()
      bind(classOf[firebaseDeletionService]).asEagerSingleton()

      // Bind Prod JWT Validator for Prod/Dev Mode
      bind(classOf[JwtValidator]).to(classOf[JwtValidatorImpl])

      // Optional Jaeger Exporter bind(classOf[JaegerTracingCreator]).asEagerSingleton()

    } else {

      logger.info(s"- - - Binding Services for for Prod Mode - - -")

      bind(classOf[firebaseCreationService]).asEagerSingleton()
      bind(classOf[firebaseDeletionService]).asEagerSingleton()

      // Bind Prod JWT Validator for Prod/Dev Mode
      bind(classOf[JwtValidator]).to(classOf[JwtValidatorImpl])

      // Tracing
      bind(classOf[StackdriverTracingCreator]).asEagerSingleton()
      bind(classOf[LoggingTracingCreator]).asEagerSingleton()

    }

  }

}

@Singleton
class LoggingTracingCreator @Inject() (lifecycle: ApplicationLifecycle) {
  LoggingTraceExporter.register()
  lifecycle.addStopHook { () =>
    Future.successful(LoggingTraceExporter.unregister())
  }
}

@Singleton
class JaegerTracingCreator @Inject() (lifecycle: ApplicationLifecycle) {
  val jaegerExporterConfiguration: JaegerExporterConfiguration = JaegerExporterConfiguration
    .builder()
    .setServiceName("bootstrap-play2")
    .setThriftEndpoint("http://127.0.0.1:14268/api/traces")
    .build()
  JaegerTraceExporter.createAndRegister(jaegerExporterConfiguration)

  lifecycle.addStopHook { () =>
    Future.successful(JaegerTraceExporter.unregister())
  }
}

@Singleton
class StackdriverTracingCreator @Inject() (lifecycle: ApplicationLifecycle, config: Config) {
  val serviceAccount: InputStream                                   = getClass.getClassLoader.getResourceAsStream(config.getString("firebase.file"))
  val credentials: GoogleCredentials                                = GoogleCredentials.fromStream(serviceAccount)
  val stackDriverTraceExporterConfig: StackdriverTraceConfiguration = StackdriverTraceConfiguration
    .builder()
    .setProjectId(config.getString("project.id"))
    .setCredentials(credentials)
    .setFixedAttributes(
      Map(
        ("/component", AttributeValue.stringAttributeValue("PlayServer"))
      ).asJava
    )
    .build()

  StackdriverTraceExporter.createAndRegister(stackDriverTraceExporterConfig)
  lifecycle.addStopHook { () =>
    Future.successful(StackdriverTraceExporter.unregister())
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
class DAOCloseHook @Inject() (companiesDAO: CompaniesDAO, locationsDAO: LocationsDAO, lifecycle: ApplicationLifecycle) {
  lifecycle.addStopHook { () =>
    Future.successful({
      companiesDAO.close()
      locationsDAO.close()
    })
  }
}

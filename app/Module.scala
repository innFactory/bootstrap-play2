import akka.actor.ActorSystem
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.scaladsl.AkkaManagement
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.auth.internal.Utils.isEmulatorMode

import javax.inject.{Inject, Provider, Singleton}
import com.typesafe.config.Config
import play.api.inject.ApplicationLifecycle
import play.api.{Configuration, Environment, Logger, Mode}
import slick.jdbc.JdbcBackend.Database
import com.google.inject.AbstractModule
import de.innfactory.auth.firebase.FirebaseBase
import de.innfactory.auth.firebase.validator.{JwtValidator, JwtValidatorImpl}
import de.innfactory.play.flyway.FlywayMigrator
import io.opencensus.exporter.trace.logging.LoggingTraceExporter
import io.opencensus.exporter.trace.stackdriver.{StackdriverTraceConfiguration, StackdriverTraceExporter}
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

    logger.info(s"- - - Binding Firebase - - -")

    // bind(classOf[firebaseCreationService]).asEagerSingleton()
    // bind(classOf[firebaseDeletionService]).asEagerSingleton()
    bind(classOf[JwtValidator]).to(classOf[JwtValidatorImpl])
    bind(classOf[AkkaCluster]).asEagerSingleton()

    /**
     * Inject Modules depended on environment (Test, Prod, Dev)
     */
    if (environment.mode == Mode.Test) {
      logger.info(s"- - - Binding Services for for Test Mode - - -")
    } else if (environment.mode == Mode.Dev) {
      logger.info(s"- - - Binding Services for for Dev Mode - - -")
    } else {
      logger.info(s"- - - Binding Services for for Prod Mode - - -")
      // Tracing
      bind(classOf[StackdriverTracingCreator]).asEagerSingleton()
      bind(classOf[LoggingTracingCreator]).asEagerSingleton()
    }

  }

}

@Singleton
class AkkaCluster @Inject() (system: ActorSystem) {
  AkkaManagement(system).start()
  ClusterBootstrap(system).start()
}

@Singleton
class LoggingTracingCreator @Inject() (lifecycle: ApplicationLifecycle) {
  LoggingTraceExporter.register()
  lifecycle.addStopHook { () =>
    Future.successful(LoggingTraceExporter.unregister())
  }
}

@Singleton
class StackdriverTracingCreator @Inject() (lifecycle: ApplicationLifecycle, config: Config) {
  val serviceAccount: InputStream = getClass.getClassLoader.getResourceAsStream(config.getString("firebase.file"))
  val credentials: GoogleCredentials = GoogleCredentials.fromStream(serviceAccount)
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
class FlywayMigratorImpl @Inject() (env: Environment, configuration: Configuration, config: Config)
    extends FlywayMigrator(configuration, env, configIdentifier = s"${config.getString("project.id")}.database")

/** Creates FirebaseApp on Application creation */
class firebaseCreationService @Inject() (config: Config, env: Environment) {
  println(System.getenv("FIREBASE_AUTH_EMULATOR_HOST"))
  println(isEmulatorMode)
  FirebaseBase.instantiateFirebase(config.getString("firebase.file"), config.getString("project.id"))
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


import javax.inject.{Inject, Provider, Singleton}
import com.google.inject.AbstractModule
import com.typesafe.config.Config
import common.jwt.FirebaseJWTValidator
import models.db._
import play.api.inject.ApplicationLifecycle
import play.api.{Configuration, Environment}
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.Future

/**
 * This module handles the bindings for the API to the Slick implementation.
 *
 * https://www.playframework.com/documentation/latest/ScalaDependencyInjection#Programmatic-bindings
 */
class Module(environment: Environment,
             configuration: Configuration) extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Database]).toProvider(classOf[DatabaseProvider])
    bind(classOf[ContactDAO]).to(classOf[SlickContactDAO])
    bind(classOf[ContactDAOCloseHook]).asEagerSingleton()
    bind(classOf[firebaseCreationService]).asEagerSingleton()
    bind(classOf[firebaseDeletionService]).asEagerSingleton()
    bind(classOf[configuration]).asEagerSingleton()
  }
}

class configuration @Inject() (config: Config) {
  config
}

/** Creates FirebaseApp on Application creation */
class firebaseCreationService @Inject() (config: Config) {
  FirebaseJWTValidator.instanciateFirebase(config.getString("firebase.file"), "https://innfactory-inntend.firebaseio.com/")
}

/** Deletes FirebaseApp safely. Important on dev restart. */
class firebaseDeletionService @Inject()(lifecycle: ApplicationLifecycle) {
  lifecycle.addStopHook { () =>
    Future.successful(FirebaseJWTValidator.deleteFirebase())
  }
}

@Singleton
class DatabaseProvider @Inject() (config: Config) extends Provider[Database] {
  lazy val get = Database.forConfig("innSide.database", config)
}

/** Closes database connections safely.  Important on dev restart. */
class ContactDAOCloseHook @Inject()(dao: ContactDAO, lifecycle: ApplicationLifecycle) {
  lifecycle.addStopHook { () =>
    Future.successful(dao.close())
  }
}



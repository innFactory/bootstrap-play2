package de.innfactory.bootstrapplay2.db

import play.api.inject.ApplicationLifecycle
import slick.jdbc.JdbcBackend.Database

import java.sql.Connection
import javax.inject.{ Inject, Singleton }
import scala.concurrent.Future

@Singleton
class DatabaseHealthSocket @Inject() (db: Database, lifecycle: ApplicationLifecycle) {
  private val connection: Connection = db.source.createConnection()

  def isConnectionOpen: Boolean = connection.getSchema.nonEmpty

  lifecycle.addStopHook { () =>
    Future.successful(connection.close())
  }
}

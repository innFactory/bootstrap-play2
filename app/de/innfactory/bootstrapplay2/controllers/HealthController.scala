package de.innfactory.bootstrapplay2.controllers

import de.innfactory.bootstrapplay2.db.DatabaseHealthSocket

import javax.inject.{ Inject, Singleton }
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class HealthController @Inject() (
  cc: ControllerComponents,
  databaseHealthSocket: DatabaseHealthSocket
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {
  def ping: Action[AnyContent] =
    Action {
      if (databaseHealthSocket.isConnectionOpen)
        Ok("Ok")
      else
        InternalServerError("Database Connection Lost")

    }
}

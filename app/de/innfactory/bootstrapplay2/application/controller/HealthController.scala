package de.innfactory.bootstrapplay2.application.controller

import de.innfactory.bootstrapplay2.commons.infrastructure.DatabaseHealthSocket
import de.innfactory.play.controller.BaseController
import play.api.mvc.{ AbstractController, Action, AnyContent, ControllerComponents }

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class HealthController @Inject() (
  databaseHealthSocket: DatabaseHealthSocket
)(implicit ec: ExecutionContext, cc: ControllerComponents)
    extends BaseController {
  def ping: Action[AnyContent] =
    Action {
      if (databaseHealthSocket.isConnectionOpen)
        Ok("Ok")
      else
        InternalServerError("Database Connection Lost")
    }
}

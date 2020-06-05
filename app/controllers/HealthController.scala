package controllers

import javax.inject.{ Inject, Singleton }
import play.api.mvc._
import scala.concurrent.ExecutionContext

@Singleton
class HealthController @Inject()(
  cc: ControllerComponents
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {
  def ping: Action[AnyContent] = Action {
    Ok("Ok")
  }
}

package de.innfactory.bootstrapplay2.actions

import com.google.inject.Inject
import de.innfactory.auth.firebase.validator.JwtValidator
import de.innfactory.bootstrapplay2.common.implicits.JWT.JwtTokenGenerator
import de.innfactory.play.tracing.{ BaseAuthHeaderRefineAction, RequestWithTrace }
import play.api.mvc._

import scala.concurrent.ExecutionContext

class JwtValidationAction @Inject() (
  parser: BodyParsers.Default,
  jwtValidator: JwtValidator
)(implicit
  ec: ExecutionContext
) extends BaseAuthHeaderRefineAction[RequestWithTrace](parser) {

  override def checkAuthHeader(authHeader: String): Boolean = {
    val jwtToken = authHeader.toJwtToken

    jwtValidator.validate(jwtToken) match {
      case Left(_)  => false
      case Right(_) => true
    }
  }

}

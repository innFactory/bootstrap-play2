package de.innfactory.bootstrapplay2.actions

import com.google.inject.Inject
import de.innfactory.auth.firebase.validator.JwtValidator
import de.innfactory.bootstrapplay2.common.implicits.JWT.JwtTokenGenerator
import de.innfactory.play.tracing.{ BaseAuthHeaderRefineAction, RequestWithTrace }
import play.api.mvc.BodyParsers

import scala.concurrent.ExecutionContext

class JwtValidationAction @Inject() (
  parser: BodyParsers.Default,
  jwtValidator: JwtValidator
)(implicit
  ec: ExecutionContext
) extends BaseAuthHeaderRefineAction[RequestWithTrace](parser) {

  override def checkAuthHeader(authHeader: String): Boolean = {
    val jwtToken = authHeader.toJwtToken
    val res      = jwtValidator.validate(jwtToken) match {
      case Left(_)  => false
      case Right(_) => true
    }
    println("Auth Header Check on " + authHeader + "  " + res)
    res
  }

}

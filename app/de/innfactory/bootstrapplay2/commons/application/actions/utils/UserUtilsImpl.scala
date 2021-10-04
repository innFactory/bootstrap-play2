package de.innfactory.bootstrapplay2.commons.application.actions.utils

import cats.implicits.catsSyntaxEitherId
import de.innfactory.bootstrapplay2.commons.jwt.JWTToken
import de.innfactory.bootstrapplay2.commons.results.Results.Result
import de.innfactory.bootstrapplay2.commons.results.errors.Errors.Forbidden
import de.innfactory.play.controller.ResultStatus
import play.libs.Json

import java.util.Base64
import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

class UserUtilsImpl @Inject() (implicit ec: ExecutionContext) extends UserUtils {
  def extractUserId(authorizationHeader: Option[JWTToken]): Future[Result[String]]  =
    try {
      val tokenContent = authorizationHeader.get.content.split('.')(1)
      val decoded      = new String(Base64.getDecoder.decode(tokenContent))
      val userId       = Json.parse(decoded).get("user_id").asText()
      Future(userId.asRight[ResultStatus])
    } catch {
      case _: Exception => Future(Forbidden("").asLeft[String])
    }
  def validateJwtToken(authorizationHeader: Option[JWTToken]): Future[Result[Unit]] =
    Future(().asRight[ResultStatus])
}

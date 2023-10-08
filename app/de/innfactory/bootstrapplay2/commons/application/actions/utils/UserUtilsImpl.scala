package de.innfactory.bootstrapplay2.commons.application.actions.utils

import cats.data.EitherT
import cats.implicits.catsSyntaxEitherId
import de.innfactory.bootstrapplay2.users.domain.models.{User, UserId}
import de.innfactory.bootstrapplay2.users.domain.services.DomainUserService
import de.innfactory.play.results.errors.Errors.Forbidden
import de.innfactory.play.controller.ResultStatus
import de.innfactory.play.results.Results.Result
import de.innfactory.play.smithy4play.JWTToken
import play.libs.Json

import java.util.Base64
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserUtilsImpl @Inject() (domainUserService: DomainUserService)(implicit ec: ExecutionContext)
    extends UserUtils[User] {
  def extractUserId(authorizationHeader: Option[JWTToken]): Future[Result[String]] =
    try {
      val tokenContent = authorizationHeader.get.content.split('.')(1)
      val decoded = new String(Base64.getDecoder.decode(tokenContent))
      val userId = Json.parse(decoded).get("user_id").asText()
      Future(userId.asRight[ResultStatus])
    } catch {
      case _: Exception => Future(Forbidden("").asLeft[String])
    }
  def validateJwtToken(authorizationHeader: Option[JWTToken]): Future[Result[Unit]] =
    Future(().asRight[ResultStatus])

  override def getUser(authorizationHeader: Option[JWTToken]): Future[Result[User]] = {
    for {
      userId <- EitherT(extractUserId(authorizationHeader))
      user <- domainUserService.getUserByIdWithoutRequestContext(UserId(userId))
    } yield user
  }.value
}

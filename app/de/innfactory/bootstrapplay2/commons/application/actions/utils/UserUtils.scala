package de.innfactory.bootstrapplay2.commons.application.actions.utils

import com.google.inject.ImplementedBy
import de.innfactory.bootstrapplay2.commons.jwt.JWTToken
import de.innfactory.bootstrapplay2.commons.results.Results.Result

import scala.concurrent.Future

@ImplementedBy(classOf[UserUtilsImpl])
trait UserUtils {
  def extractUserId(authorizationHeader: Option[JWTToken]): Future[Result[String]]
  def validateJwtToken(authorizationHeader: Option[JWTToken]): Future[Result[Unit]]
}

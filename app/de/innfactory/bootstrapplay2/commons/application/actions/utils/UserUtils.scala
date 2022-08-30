package de.innfactory.bootstrapplay2.commons.application.actions.utils

import com.google.inject.ImplementedBy
import de.innfactory.play.results.Results.Result
import de.innfactory.play.smithy4play.JWTToken

import scala.concurrent.Future

@ImplementedBy(classOf[UserUtilsImpl])
trait UserUtils {
  def extractUserId(authorizationHeader: Option[JWTToken]): Future[Result[String]]
  def validateJwtToken(authorizationHeader: Option[JWTToken]): Future[Result[Unit]]
}

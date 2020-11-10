package de.innfactory.bootstrapplay2.common.authorization

import java.util.Base64

import com.google.inject.Inject
import de.innfactory.auth.firebase.validator.JwtToken
import play.api.{ Configuration, Environment, Logger }

import scala.concurrent.{ ExecutionContext, Future }
import play.api.mvc._
import play.libs.Json

class FirebaseEmailExtractor[A] @Inject() (val parser: BodyParsers.Default, environment: Environment)(implicit
  val executionContext: ExecutionContext,
  configuration: Configuration
) {

  /**
   * Get Email from request
   * Takes the request header "Authorization" and extracts the JWT token,
   * then decodes the token to get the user email
   * @param request
   * @tparam A
   * @return Option[String]
   */
  def extractEmail[A](request: Request[A]): Option[String] = {
    val optionToken: Option[JwtToken] = extractToken(request.headers.get("Authorization"))
    extractEmailFromToken(optionToken)
  }

  private def extractToken(authorizationHeader: Option[String]): Option[JwtToken] =
    authorizationHeader match {
      case Some(header) if environment.mode.toString == "Test" => handleTestEnvToken(header)
      case Some(header)                                        => handleHeaderProdToken(header)
      case None                                                => None
    }

  private def handleHeaderProdToken(header: String): Option[JwtToken] =
    header match {
      case x: String if x.startsWith("Bearer") =>
        Some(JwtToken(x.splitAt(7)._2))
      case x                                   => Some(JwtToken(x))
    }

  private def handleTestEnvToken(header: String): Option[JwtToken] =
    if (header == "empty@empty.de") None
    else Some(JwtToken(header))

  private def extractEmailFromToken(optionToken: Option[JwtToken]): Option[String] =
    // In Test Environment the token will be returned as email
    optionToken match {
      case Some(token) if environment.mode.toString == "Test" =>
        Some(token.content)
      case Some(token)                                        => extractEmailFromProdToken(token)
      case None                                               => None
    }

  private def extractEmailFromProdToken(token: JwtToken): Some[String] = {
    val tokenCont = token.content.split('.')(1)
    val decoded   = new String(Base64.getDecoder.decode(tokenCont))
    Some(Json.parse(decoded).get("email").asText())
  }

}

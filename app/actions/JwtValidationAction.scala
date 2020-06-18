package actions

import com.google.inject.Inject
import com.nimbusds.jwt.proc.BadJWTException
import play.api.Environment
import play.api.mvc.Results.Forbidden
import play.api.mvc.Results.Unauthorized

import scala.concurrent.{ ExecutionContext, Future }
import play.api.mvc._
import firebaseAuth.{ JwtToken, JwtValidator }

class JwtValidationAction @Inject()(parser: BodyParsers.Default, jwtValidator: JwtValidator, environment: Environment)(
  implicit ec: ExecutionContext
) extends ActionBuilderImpl(parser) {
  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) =
    if (extractAndCheckAuthHeader(request.headers).getOrElse(false)) {
      block(request)
    } else if (request.headers.get("Authorization").isEmpty) {
      Future.successful(Unauthorized("Unauthorized"))
    } else {
      Future.successful(Forbidden("Forbidden"))
    }

  /**
   * Extract auth header from requestHeaders
   * @param requestHeader
   * @return
   */
  def extractAndCheckAuthHeader(requestHeader: Headers) =
    for {
      header <- requestHeader.get("Authorization")
    } yield {
      checkAuthHeader(header)
    }

  /**
   * check and validate auth header
   * @param authHeader
   * @return
   */
  def checkAuthHeader(authHeader: String): Boolean =
    // In Test env, jwt will not be validated
    if (environment.mode.toString != "Test") {
      val jwtToken = authHeader match {
        case token: String if token.startsWith("Bearer") =>
          JwtToken(token.splitAt(7)._2)
        case token => JwtToken(token)
      }

      jwtValidator.validate(jwtToken) match {
        case Left(error: BadJWTException) => {
          false
        }
        case Right(_) => true
      }
    } else {
      true
    }

}

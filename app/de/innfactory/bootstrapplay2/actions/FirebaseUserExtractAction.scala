package de.innfactory.bootstrapplay2.actions

import cats.implicits.catsSyntaxEitherId
import com.google.inject.Inject
import de.innfactory.auth.firebase.implicits.JwtImplicits.JwtTokenEnhancer
import de.innfactory.auth.firebase.validator.JwtToken
import de.innfactory.bootstrapplay2.common.implicits.JWT.JwtTokenGenerator
import de.innfactory.bootstrapplay2.common.results.ErrorResponse
import de.innfactory.bootstrapplay2.services.firebase.FirebaseUserService
import de.innfactory.bootstrapplay2.services.firebase.models.FirebaseUser
import de.innfactory.play.tracing.{ RequestWithTrace, TraceRequest, UserExtractionActionBase }
import io.opencensus.trace.Span
import play.api.Environment
import play.api.mvc.Results.{ FailedDependency, Unauthorized }
import play.api.mvc._

import scala.concurrent.{ ExecutionContext, Future }

class RequestWithFirebaseUser[A](val firebaseUser: FirebaseUser, val request: Request[A], implicit val traceSpan: Span)
    extends WrappedRequest[A](request)
    with TraceRequest[A]

class FirebaseUserExtractAction @Inject() (
  firebaseUserService: FirebaseUserService
)(implicit executionContext: ExecutionContext, parser: BodyParsers.Default, environment: Environment)
    extends UserExtractionActionBase[RequestWithTrace, RequestWithFirebaseUser] {

  def extractFirebaseUserIdFromRequest[A](request: Request[A]): Option[String] = {
    val token = extractToken(request.headers.get("Authorization"))
    token match {
      case Some(token) => token.getUserId
      case None        => None
    }
  }

  private def extractToken(authorizationHeader: Option[String]): Option[JwtToken] =
    authorizationHeader match {
      case Some(header) => handleHeaderProdToken(header)
      case None         => None
    }

  private def handleHeaderProdToken(header: String): Option[JwtToken] = Some(header.toJwtToken)

  override def extractUserAndCreateNewRequest[A](request: RequestWithTrace[A])(implicit
    environment: Environment,
    parser: BodyParsers.Default,
    executionContext: ExecutionContext
  ): Future[Either[Result, RequestWithFirebaseUser[A]]] = {
    val firebaseUserId = extractFirebaseUserIdFromRequest(request)
    val result         = firebaseUserId match {
      case Some(id) =>
        val userRecord = firebaseUserService._internal_getUser(id)
        if (userRecord.emailVerified)
          new RequestWithFirebaseUser[A](userRecord, request, request.traceSpan).asRight[Result]
        else FailedDependency(ErrorResponse.fromMessage("Email not verified")).asLeft[RequestWithFirebaseUser[A]]
      case None     =>
        Unauthorized(ErrorResponse.fromMessage("Access without Authorization")).asLeft[RequestWithFirebaseUser[A]]
    }
    Future(result)
  }

}

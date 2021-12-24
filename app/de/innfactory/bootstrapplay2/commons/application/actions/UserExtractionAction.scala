package de.innfactory.bootstrapplay2.commons.application.actions

import cats.data.{EitherT, Validated}
import cats.implicits.{catsSyntaxEitherId, _}
import com.google.inject.Inject
import de.innfactory.bootstrapplay2.commons.results.Results
import de.innfactory.bootstrapplay2.commons.application.actions.models.RequestWithUser
import de.innfactory.bootstrapplay2.commons.application.actions.utils.UserUtils
import de.innfactory.bootstrapplay2.commons.jwt.JWTToken
import de.innfactory.bootstrapplay2.users.domain.interfaces.UserService
import de.innfactory.bootstrapplay2.users.domain.models.{User, UserId}
import de.innfactory.play.controller.ErrorResponse
import de.innfactory.play.tracing.{RequestWithTrace, UserExtractionActionBase}
import play.api.Environment
import play.api.mvc.Results.{FailedDependency, NotFound, Unauthorized}
import play.api.mvc.{BodyParsers, Request, Result}

import scala.concurrent.{ExecutionContext, Future}

private[actions] class UserExtractionAction @Inject() (
    userService: UserService,
    userUtils: UserUtils
)(implicit executionContext: ExecutionContext, parser: BodyParsers.Default, environment: Environment)
    extends UserExtractionActionBase[RequestWithTrace, RequestWithUser] {

  override def extractUserAndCreateNewRequest[A](request: RequestWithTrace[A])(implicit
      environment: Environment,
      parser: BodyParsers.Default,
      executionContext: ExecutionContext
  ): Future[Either[Result, RequestWithUser[A]]] = {
    val userIdMatch = extractUserIdFromRequest(request)
    userIdMatch.flatMap {
      case Left(_) =>
        Future(
          Unauthorized(ErrorResponse.fromMessage("Access without Authorization")).asLeft[RequestWithUser[A]]
        )
      case Right(id) => getUserAndCreateRequest(id, request).value
    }
  }

  private def getUserAndCreateRequest[A](
      id: String,
      request: RequestWithTrace[A]
  ): EitherT[Future, Result, RequestWithUser[A]] =
    for {
      userRecord <- EitherT(getUser(id))
      result <- EitherT(Future(validateUser(userRecord, request)))
    } yield result

  private def getUser[A](id: String): Future[Either[Result, User]] =
    userService
      .getUserByIdWithoutRequestContext(UserId(id))
      .map(_.leftMap[Result](_ => NotFound(ErrorResponse.fromMessage("User not Found"))))

  private def validateUser[A](userRecord: User, request: RequestWithTrace[A]): Either[Result, RequestWithUser[A]] =
    Validated
      .cond[Result, RequestWithUser[A]](
        userRecord.emailVerified,
        new RequestWithUser[A](
          userRecord,
          request,
          request.traceSpan
        ),
        FailedDependency(
          ErrorResponse.fromMessage("Email not verified")
        )
      )
      .toEither

  private def extractUserIdFromRequest[A](request: Request[A]): Future[Results.Result[String]] = {
    val token = request.headers.get("Authorization").map(JWTToken)
    for {
      _ <- userUtils.validateJwtToken(token)
      id <- userUtils.extractUserId(token)
    } yield id
  }

}

package de.innfactory.bootstrapplay2.application.controller

import cats.data.{EitherT, Kleisli}
import de.innfactory.bootstrapplay2.commons.{RequestContext, RequestContextWithUser}
import de.innfactory.bootstrapplay2.commons.application.actions.utils.UserUtils
import de.innfactory.bootstrapplay2.users.domain.models.User
import de.innfactory.play.controller.{ErrorResult, ResultStatus}
import de.innfactory.smithy4play.{ContextRouteError, RoutingContext}
import play.api.Application
import de.innfactory.play.smithy4play.{AbstractBaseController, HttpHeaders}
import de.innfactory.play.smithy4play.ImplicitLogContext

import scala.concurrent.ExecutionContext

class BaseController(implicit ec: ExecutionContext, app: Application)
    extends AbstractBaseController[ResultStatus, RequestContextWithUser, RequestContext]
    with ImplicitLogContext
    with BaseMapper {

  private val userUtils = app.injector.instanceOf[UserUtils[User]]

  override def AuthAction: Kleisli[ApplicationRouteResult, RequestContext, RequestContextWithUser] = Kleisli {
    context =>
      val result = for {
        _ <- EitherT(userUtils.validateJwtToken(context.httpHeaders.authAsJwt))
        user <- EitherT(userUtils.getUser(context.httpHeaders.authAsJwt))
      } yield RequestContextWithUser(context.httpHeaders, context.span, user)
      result
  }

  override def errorHandler(e: ResultStatus): ContextRouteError =
    e match {
      case result: ErrorResult =>
        new ContextRouteError {
          override def message: String = result.message
          override def additionalInfoToLog: Option[String] = result.additionalInfoToLog
          override def additionalInfoErrorCode: Option[String] = result.additionalInfoErrorCode
          override def statusCode: Int = result.statusCode

          override def additionalInformation: Option[String] = None
        }
    }

  override def createRequestContextFromRoutingContext(r: RoutingContext): RequestContext =
    new RequestContext(HttpHeaders(r.map))
}

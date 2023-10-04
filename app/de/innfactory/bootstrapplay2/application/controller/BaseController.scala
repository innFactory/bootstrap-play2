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
import play.api.libs.json.{JsValue, Json}

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

  case class ErrorJson(message: String, additionalInfoErrorCode: Option[String])

  private val writes = Json.writes[ErrorJson]

  override def errorHandler(e: ResultStatus): ContextRouteError =
    e match {
      case result: ErrorResult =>
        new ContextRouteError {
          override def message: String = result.message
          override def statusCode: Int = result.statusCode
          override def toJson: JsValue = Json.toJson(ErrorJson(result.message, result.additionalInfoErrorCode))(writes)
        }
    }

  override def createRequestContextFromRoutingContext(r: RoutingContext): RequestContext =
    new RequestContext(HttpHeaders(r.headers))
}

package de.innfactory.bootstrapplay2.controllers

import akka.stream.Materializer
import cats.data.EitherT
import de.innfactory.bootstrapplay2.actions.TracingUserAction
import de.innfactory.bootstrapplay2.common.logging.ImplicitLogContext
import de.innfactory.bootstrapplay2.common.request.ReqConverterHelper.{ requestContext, requestContextWithUser }
import de.innfactory.bootstrapplay2.repositories.UsersRepository
import de.innfactory.play.tracing.TracingAction
import play.api.mvc._
import de.innfactory.bootstrapplay2.services.firebase.models.UserUpsertRequest
import de.innfactory.bootstrapplay2.services.firebase.models.{ User, UserPasswordResetRequest }

import javax.inject.{ Inject, Singleton }
import scala.concurrent.ExecutionContext

@Singleton
class UsersController @Inject() (
  cc: ControllerComponents,
  repository: UsersRepository,
  tracingAction: TracingAction,
  tracingUserAction: TracingUserAction
)(implicit ec: ExecutionContext, mat: Materializer)
    extends AbstractController(cc)
    with ImplicitLogContext {

  def getUsersForCompany(companyId: Long): Action[AnyContent] =
    tracingUserAction("getUsersForAuthority").async { implicit request =>
      val result = for {
        source <- EitherT(repository.getUsersForCompany(companyId)(requestContextWithUser))
      } yield source

      result.value.completeSourceChunked()
    }

  def createUser: Action[AnyContent] =
    tracingUserAction("createUser").async { implicit request =>
      print(request)
      val json   = request.body.asJson.get
      val entity = json.as[UserUpsertRequest]
      val result = for {
        source <- EitherT(repository.upsertUser(entity)(requestContextWithUser))
      } yield source
      result.value.completeResult()

    }

  def patchUser: Action[AnyContent] =
    tracingUserAction("patchUser").async { implicit request =>
      val json   = request.body.asJson.get
      val entity = json.as[UserUpsertRequest]
      val result = for {
        source <- EitherT(repository.patchUser(entity)(requestContextWithUser))
      } yield source
      result.value.completeResult()
    }

  def resetPassword: Action[AnyContent] =
    tracingAction("resetPassword").async { implicit request =>
      val json   = request.body.asJson.get
      val entity = json.as[UserPasswordResetRequest]
      val result = for {
        source <-
          EitherT(repository.resetPassword(entity.token, entity.password, entity.userId)(requestContext))
      } yield source
      result.value.completeResult()

    }

}

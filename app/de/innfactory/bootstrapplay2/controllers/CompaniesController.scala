package de.innfactory.bootstrapplay2.controllers

import akka.stream.scaladsl.Source

import java.util.UUID
import de.innfactory.bootstrapplay2.actions.TracingUserAction
import cats.data.EitherT
import cats.implicits._
import de.innfactory.bootstrapplay2.common.request.ReqConverterHelper.{ requestContext, requestContextWithUser }
import de.innfactory.bootstrapplay2.common.results.Results.ResultStatus
import de.innfactory.play.tracing.TracingAction

import javax.inject.{ Inject, Singleton }
import de.innfactory.bootstrapplay2.models.api.Company
import play.api.mvc._
import de.innfactory.bootstrapplay2.models.api.Company._
import de.innfactory.bootstrapplay2.repositories.CompaniesRepository
import de.innfactory.bootstrapplay2.common.validators.JsonValidator._
import de.innfactory.bootstrapplay2.services.firebase.utils.Utils._

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class CompaniesController @Inject() (
  cc: ControllerComponents,
  tracingAction: TracingAction,
  tracingUserAction: TracingUserAction,
  companiesRepository: CompaniesRepository
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  def getMe: Action[AnyContent] =
    tracingUserAction("getMe Company").async { implicit request =>
      val result: EitherT[Future, ResultStatus, Company] = for {
        companyId    <- EitherT(Future(request.firebaseUser.getCompanyId()))
        lookupResult <- EitherT(companiesRepository.lookup(companyId)(requestContextWithUser))
      } yield lookupResult
      result.value.completeResult()
    }

  def getSingle(
    id: Long
  ): Action[AnyContent] =
    tracingUserAction("get Company").async { implicit request =>
      companiesRepository.lookup(id)(requestContextWithUser).completeResult()
    }

  def getStreamed =
    tracingAction("get companies streamed").async { implicit request =>
      val result =
        EitherT.right[ResultStatus](companiesRepository.streamedAll(requestContext).map(Source.fromPublisher(_)))
      result.value.completeSourceChunked()
    }

  def patch: Action[AnyContent] =
    tracingUserAction("patch Company").async { implicit request =>
      val json                                           = request.body.asJson.get
      val entity                                         = json.as[Company]
      val result: EitherT[Future, ResultStatus, Company] = for {
        _       <- EitherT(Future(json.validateFor))
        created <- EitherT(companiesRepository.patch(entity)(requestContextWithUser))
      } yield created
      result.value.completeResult()
    }

  def post: Action[AnyContent] =
    tracingUserAction("post Company").async { implicit request =>
      val json                                           = request.body.asJson.get
      val entity                                         = json.as[Company]
      val result: EitherT[Future, ResultStatus, Company] = for {
        _       <- EitherT(Future(json.validateFor[Company]))
        created <- EitherT(companiesRepository.post(entity)(requestContextWithUser))
      } yield created
      result.value.completeResult()
    }

  def delete(id: Long): Action[AnyContent] =
    tracingUserAction("delete Company").async { implicit request =>
      companiesRepository.delete(id)(requestContextWithUser).completeResultWithoutBody(204)
    }

}

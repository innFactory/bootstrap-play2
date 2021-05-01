package de.innfactory.bootstrapplay2.controllers

import akka.stream.scaladsl.Source
import java.util.UUID
import de.innfactory.bootstrapplay2.actions.TracingCompanyAction
import cats.data.EitherT
import cats.implicits._
import de.innfactory.bootstrapplay2.common.request.ReqConverterHelper.{ requestContext, requestContextWithCompany }
import de.innfactory.bootstrapplay2.common.results.Results.ResultStatus
import de.innfactory.play.tracing.TracingAction
import javax.inject.{ Inject, Singleton }
import de.innfactory.bootstrapplay2.models.api.Company
import play.api.mvc._
import de.innfactory.bootstrapplay2.models.api.Company._
import de.innfactory.bootstrapplay2.repositories.CompaniesRepository
import de.innfactory.bootstrapplay2.common.validators.JsonValidator._

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class CompaniesController @Inject() (
  cc: ControllerComponents,
  tracingAction: TracingAction,
  tracingCompanyAction: TracingCompanyAction,
  companiesRepository: CompaniesRepository
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  def getMe: Action[AnyContent] =
    tracingCompanyAction("getMe Company").async { implicit request =>
      Future(request.company.asRight[ResultStatus]).completeResult()
    }

  def getSingle(
    id: String
  ): Action[AnyContent] =
    tracingCompanyAction("get Company").async { implicit request =>
      companiesRepository.lookup(UUID.fromString(id))(requestContextWithCompany).completeResult()
    }

  def getStreamed =
    tracingAction("get companies streamed").async { implicit request =>
      val result =
        EitherT.right[ResultStatus](companiesRepository.streamedAll(requestContext).map(Source.fromPublisher(_)))
      result.value.completeSourceChunked()
    }

  def patch: Action[AnyContent] =
    tracingCompanyAction("patch Company").async { implicit request =>
      val json                                           = request.body.asJson.get
      val entity                                         = json.as[Company]
      val result: EitherT[Future, ResultStatus, Company] = for {
        _       <- EitherT(Future(json.validateFor))
        created <- EitherT(companiesRepository.patch(entity)(requestContextWithCompany))
      } yield created
      result.value.completeResult()
    }

  def post: Action[AnyContent] =
    tracingAction("post Company").async { implicit request =>
      val json                                           = request.body.asJson.get
      val entity                                         = json.as[Company]
      val result: EitherT[Future, ResultStatus, Company] = for {
        _       <- EitherT(Future(json.validateFor[Company]))
        created <- EitherT(companiesRepository.post(entity)(requestContext))
      } yield created
      result.value.completeResult()
    }

  def delete(id: String): Action[AnyContent] =
    tracingCompanyAction("delete Company").async { implicit request =>
      companiesRepository.delete(UUID.fromString(id))(requestContextWithCompany).completeResultWithoutBody(204)
    }

}

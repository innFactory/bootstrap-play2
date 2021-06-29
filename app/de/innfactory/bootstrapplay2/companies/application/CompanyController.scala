package de.innfactory.bootstrapplay2.companies.application

import akka.NotUsed
import akka.stream.scaladsl.Source
import cats.data.EitherT
import de.innfactory.bootstrapplay2.application.controller.BaseController
import de.innfactory.bootstrapplay2.commons.ReqConverterHelper.requestContextWithUser
import de.innfactory.bootstrapplay2.commons.logging.ImplicitLogContext
import de.innfactory.bootstrapplay2.commons.application.actions.TracingUserAction
import de.innfactory.bootstrapplay2.commons.results.Results
import de.innfactory.bootstrapplay2.companies.domain.interfaces.CompanyService
import play.api.mvc.{ Action, AnyContent, ControllerComponents }
import de.innfactory.bootstrapplay2.companies.application.models.{ CompanyRequest, CompanyResponse }
import de.innfactory.bootstrapplay2.companies.domain.models.{ Company, CompanyId }

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

class CompanyController @Inject() (tracingUserAction: TracingUserAction, companyService: CompanyService)(implicit
  ec: ExecutionContext,
  cc: ControllerComponents
) extends BaseController
    with ImplicitLogContext {

  def getAll: Action[AnyContent] = tracingUserAction().async { implicit request =>
    val result: EitherT[Future, Results.ResultStatus, Seq[CompanyResponse]] = for {
      getAll <- companyService.getAll()(requestContextWithUser)
    } yield getAll.map(CompanyResponse.fromCompany)
    result.value.completeResult()
  }

  def getAllCompaniesAsSource: Action[AnyContent] = tracingUserAction().async { implicit request =>
    val result: EitherT[Future, Results.ResultStatus, Source[CompanyResponse, NotUsed]] = for {
      getAll <- companyService.getAllCompaniesAsStream()(requestContextWithUser)
    } yield getAll.map(CompanyResponse.fromCompany)
    result.value.completeSourceChunked()
  }

  def getById(id: Long): Action[AnyContent] = tracingUserAction().async { implicit request =>
    val companyId = CompanyId(id)
    val result    = for {
      company <- companyService.getById(companyId)(requestContextWithUser)
    } yield CompanyResponse.fromCompany(company)
    result.value.completeResult()
  }

  def create(): Action[CompanyRequest] = tracingUserAction().async(validateJson[CompanyRequest]) { implicit request =>
    val companyRequest = request.request.body
    val result         = for {
      company <- companyService.createCompany(companyRequest.toCompany())(requestContextWithUser(request))
    } yield CompanyResponse.fromCompany(company)
    result.value.completeResult()
  }

  def update(): Action[CompanyRequest] = tracingUserAction().async(validateJson[CompanyRequest]) { implicit request =>
    val companyRequest = request.request.body
    val result         = for {
      company <- companyService.updateCompany(companyRequest.toCompany())(requestContextWithUser(request))
    } yield CompanyResponse.fromCompany(company)
    result.value.completeResult()
  }

  def delete(id: Long): Action[AnyContent] = tracingUserAction().async { implicit request =>
    val companyId = CompanyId(id)
    val result    = for {
      deleteResult <- companyService.deleteCompany(companyId)(requestContextWithUser)
    } yield deleteResult
    result.value.completeResultWithoutBody()
  }

}

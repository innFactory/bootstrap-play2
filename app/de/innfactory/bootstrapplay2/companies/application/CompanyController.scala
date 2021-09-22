package de.innfactory.bootstrapplay2.companies.application

import akka.NotUsed
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import cats.data.EitherT
import de.innfactory.bootstrapplay2.application.controller.BaseController
import de.innfactory.bootstrapplay2.commons.ReqConverterHelper.{requestContext, requestContextWithUser}
import de.innfactory.bootstrapplay2.commons.logging.ImplicitLogContext
import de.innfactory.bootstrapplay2.commons.application.actions.{TracingAction, TracingUserAction}
import de.innfactory.bootstrapplay2.commons.application.actions.models.{RequestWithTrace, RequestWithUser}
import de.innfactory.bootstrapplay2.commons.results.Results.ResultStatus
import de.innfactory.bootstrapplay2.companies.domain.interfaces.CompanyService
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import de.innfactory.bootstrapplay2.companies.application.models.{CompanyRequest, CompanyResponse}
import de.innfactory.bootstrapplay2.companies.domain.models.{Company, CompanyId}
import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.language.implicitConversions

class CompanyController @Inject() (tracingUserAction: TracingUserAction, companyService: CompanyService, tracingAction: TracingAction)(implicit
  ec: ExecutionContext,
  cc: ControllerComponents,
  mat: Materializer
) extends BaseController
    with ImplicitLogContext {

  implicit private def companyRequestToCompany(companyRequest: CompanyRequest): Company = companyRequest.toCompany()

  implicit private val outMapperSeq: OutMapper[Seq[Company], Seq[CompanyResponse]] =
    OutMapper[Seq[Company], Seq[CompanyResponse]](companies => companies.map(CompanyResponse.fromCompany))

  implicit private val outMapper: OutMapper[Company, CompanyResponse] =
    OutMapper[Company, CompanyResponse](CompanyResponse.fromCompany)

  implicit private val outMapperSource: OutMapper[Source[Company, NotUsed], Source[CompanyResponse, NotUsed]] =
    OutMapper[Source[Company, NotUsed], Source[CompanyResponse, NotUsed]](v => v.map(CompanyResponse.fromCompany))

  def getById(id: Long): Action[AnyContent] =
    Endpoint.in[RequestWithUser](tracingUserAction())
      .logic((_, rc) => companyService.getById(CompanyId(id))(requestContextWithUser(rc)))
      .mapOutTo[CompanyResponse]
      .result(r => r.completeResult())

  def getAll: Action[AnyContent] =
    Endpoint.in[RequestWithUser](tracingUserAction())
      .logic((_, rc) => companyService.getAll()(requestContextWithUser(rc)))
      .mapOutTo[Seq[CompanyResponse]]
      .result(_.completeResult())

  def getAllPublic(filter: Option[String]): Action[AnyContent] =
    Endpoint.in[RequestWithTrace](tracingAction())
      .logic((_, rc) => EitherT.right[ResultStatus](companyService.getAllForGraphQL(filter)(requestContext(rc))))
      .mapOutTo[Seq[CompanyResponse]]
      .result(_.completeResult())

  def delete(id: Long): Action[AnyContent] =
    Endpoint.in[RequestWithUser](tracingUserAction())
      .logic((_, r) => companyService.deleteCompany(CompanyId(id))(requestContextWithUser(r)))
      .result(_.completeResultWithoutBody(statusCode = 204))

  private def UpdateCreateEndpoint(): Endpoint[CompanyRequest, CompanyResponse, RequestWithUser, Company, Company] =
    Endpoint.in[CompanyRequest, RequestWithUser, Company](tracingUserAction())
      .logic((companyRequest, rc) => companyService.createCompany(companyRequest)(requestContextWithUser(rc)))
      .mapOutTo[CompanyResponse]

  def update: Action[CompanyRequest] =
    UpdateCreateEndpoint()
      .result(_.completeResultWithoutBody(statusCode = 204))

  def create: Action[CompanyRequest] =
    UpdateCreateEndpoint()
      .result(_.completeResultWithoutBody(statusCode = 204))

  def getAllCompaniesAsSource: Action[AnyContent] =
    Endpoint.in[RequestWithUser](tracingUserAction())
      .logic[Source[Company, NotUsed]]((_, r) => companyService.getAllCompaniesAsStream()(requestContextWithUser(r)))
      .mapOutTo[Source[CompanyResponse, NotUsed]]
      .result(_.completeSourceChunked())

}

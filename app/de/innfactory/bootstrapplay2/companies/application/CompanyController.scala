package de.innfactory.bootstrapplay2.companies.application

import com.typesafe.config.Config
import de.innfactory.bootstrapplay2.application.controller.BaseController
import de.innfactory.bootstrapplay2.companies.application.mapper.CompanyMapper
import de.innfactory.bootstrapplay2.companies.domain.interfaces.CompanyService
import play.api.mvc.ControllerComponents
import de.innfactory.bootstrapplay2.companies.domain.models.CompanyId
import de.innfactory.bootstrapplay2.api.{CompaniesResponse, CompanyAPIController, CompanyRequestBody, CompanyResponse}
import de.innfactory.play.smithy4play.ImplicitLogContext
import de.innfactory.smithy4play.{AutoRouting, ContextRoute}
import play.api.Application

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import scala.language.implicitConversions

@AutoRouting
@Singleton
class CompanyController @Inject() (
    companyService: CompanyService
)(implicit
    ec: ExecutionContext,
    cc: ControllerComponents,
    app: Application,
    config: Config
) extends BaseController
    with ImplicitLogContext
    with CompanyAPIController[ContextRoute]
    with CompanyMapper {

  override def getCompanyById(companyId: de.innfactory.bootstrapplay2.api.CompanyId): ContextRoute[CompanyResponse] =
    Endpoint.withAuth
      .execute(companyService.getById(companyId)(_))
      .complete

  override def getAllCompanies(): ContextRoute[CompaniesResponse] =
    Endpoint.withAuth
      .execute(companyService.getAll()(_))
      .complete

  override def createCompany(body: CompanyRequestBody): ContextRoute[CompanyResponse] = Endpoint.withAuth
    .execute(companyService.createCompany(body)(_))
    .complete

  override def updateCompany(body: CompanyRequestBody): ContextRoute[CompanyResponse] = Endpoint.withAuth
    .execute(companyService.updateCompany(body)(_))
    .complete

  override def deleteCompany(companyId: de.innfactory.bootstrapplay2.api.CompanyId): ContextRoute[Unit] =
    Endpoint.withAuth
      .execute(companyService.deleteCompany(companyId)(_))
      .complete

}

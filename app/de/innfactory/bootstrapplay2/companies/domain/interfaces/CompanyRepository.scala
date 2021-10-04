package de.innfactory.bootstrapplay2.companies.domain.interfaces

import akka.NotUsed
import akka.stream.scaladsl.Source
import cats.data.EitherT
import com.google.inject.ImplementedBy
import de.innfactory.bootstrapplay2.commons.{ RequestContext, TraceContext }
import de.innfactory.play.controller.ResultStatus
import de.innfactory.bootstrapplay2.companies.domain.models.{ Company, CompanyId }
import de.innfactory.bootstrapplay2.companies.infrastructure.SlickCompanyRepository

import scala.concurrent.Future

@ImplementedBy(classOf[SlickCompanyRepository])
private[companies] trait CompanyRepository {

  def getAll()(implicit rc: TraceContext): EitherT[Future, ResultStatus, Seq[Company]]

  def getAllForGraphQL(filterOptions: Option[String])(implicit rc: RequestContext): Future[Seq[Company]]

  def getAllCompaniesAsSource(implicit rc: TraceContext): Source[Company, NotUsed]

  def getById(companyId: CompanyId)(implicit rc: TraceContext): EitherT[Future, ResultStatus, Company]

  def createCompany(company: Company)(implicit rc: TraceContext): EitherT[Future, ResultStatus, Company]

  def updateCompany(company: Company)(implicit rc: TraceContext): EitherT[Future, ResultStatus, Company]

  def deleteCompany(id: CompanyId)(implicit rc: TraceContext): EitherT[Future, ResultStatus, Boolean]

}

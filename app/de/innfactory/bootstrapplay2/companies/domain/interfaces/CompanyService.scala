package de.innfactory.bootstrapplay2.companies.domain.interfaces

import akka.NotUsed
import akka.stream.scaladsl.Source
import cats.data.EitherT
import com.google.inject.ImplementedBy
import de.innfactory.bootstrapplay2.commons.{ RequestContext, RequestContextWithUser, TraceContext }
import de.innfactory.bootstrapplay2.commons.results.Results.ResultStatus
import de.innfactory.bootstrapplay2.companies.domain.models.{ Company, CompanyId }
import de.innfactory.bootstrapplay2.companies.domain.services.DomainCompanyService

import scala.concurrent.Future

@ImplementedBy(classOf[DomainCompanyService])
trait CompanyService {

  def getAll()(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Seq[Company]]

  def getAllForGraphQL(filterOptions: Option[String])(implicit rc: RequestContext): Future[Seq[Company]]

  def getAllCompaniesAsStream()(implicit
    rc: RequestContextWithUser
  ): EitherT[Future, ResultStatus, Source[Company, NotUsed]]

  def getById(id: CompanyId)(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Company]

  def updateCompany(company: Company)(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Company]

  def createCompany(company: Company)(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Company]

  def deleteCompany(id: CompanyId)(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Boolean]

}

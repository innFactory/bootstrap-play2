package de.innfactory.bootstrapplay2.companies.domain.services

import akka.NotUsed
import akka.stream.scaladsl.Source
import cats.data.EitherT
import de.innfactory.bootstrapplay2.commons.{ RequestContext, RequestContextWithUser }
import de.innfactory.play.controller.ResultStatus
import de.innfactory.bootstrapplay2.companies.domain.interfaces.{ CompanyRepository, CompanyService }
import de.innfactory.bootstrapplay2.companies.domain.models.{ Company, CompanyId }

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

private[companies] class DomainCompanyService @Inject() (companyRepository: CompanyRepository)(implicit
  ec: ExecutionContext
) extends CompanyService {

  def getAll()(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Seq[Company]] =
    companyRepository.getAll()

  def getAllForGraphQL(filterOptions: Option[String])(implicit rc: RequestContext): Future[Seq[Company]] =
    companyRepository.getAllForGraphQL(filterOptions)

  def getAllCompaniesAsStream()(implicit
    rc: RequestContextWithUser
  ): EitherT[Future, ResultStatus, Source[Company, NotUsed]] =
    for {
      result <- EitherT.right[ResultStatus](Future(companyRepository.getAllCompaniesAsSource))
    } yield result

  def getById(id: CompanyId)(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Company] =
    companyRepository.getById(id)

  def updateCompany(company: Company)(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Company] =
    companyRepository.updateCompany(company)

  def createCompany(company: Company)(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Company] =
    companyRepository.createCompany(company)

  def deleteCompany(id: CompanyId)(implicit rc: RequestContextWithUser): EitherT[Future, ResultStatus, Boolean] =
    companyRepository.deleteCompany(id)
}

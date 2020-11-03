package de.innfactory.bootstrapplay2.repositories

import java.util.UUID

import de.innfactory.bootstrapplay2.actions.RequestWithCompany
import cats.implicits._
import cats.data.EitherT
import com.google.inject.ImplementedBy
import de.innfactory.bootstrapplay2.common.authorization.CompanyAuthorizationMethods
import de.innfactory.bootstrapplay2.common.results.Results.{ ErrorStatus, Result }
import de.innfactory.bootstrapplay2.db.CompaniesDAO
import de.innfactory.bootstrapplay2.graphql.ErrorParserImpl
import de.innfactory.grapqhl.play.result.implicits.GraphQlResult.EnhancedFutureResult
import javax.inject.{ Inject, Singleton }
import de.innfactory.bootstrapplay2.models.api.Company
import play.api.mvc.{ AnyContent, Request }

import scala.concurrent.{ ExecutionContext, Future }

@ImplementedBy(classOf[CompaniesRepositoryImpl])
trait CompaniesRepository {
  def lookup(id: UUID, request: RequestWithCompany[AnyContent]): Future[Result[Company]]
  def all(request: Request[AnyContent]): Future[Seq[Company]]
  def patch(company: Company, request: RequestWithCompany[AnyContent]): Future[Result[Company]]
  def post(company: Company, request: RequestWithCompany[AnyContent]): Future[Result[Company]]
  def delete(id: UUID, request: RequestWithCompany[AnyContent]): Future[Result[Company]]
}

class CompaniesRepositoryImpl @Inject() (
  companiesDAO: CompaniesDAO,
  authorizationMethods: CompanyAuthorizationMethods[AnyContent]
)(implicit ec: ExecutionContext, errorParser: ErrorParserImpl)
    extends CompaniesRepository {

  def lookup(id: UUID, request: RequestWithCompany[AnyContent]): Future[Result[Company]] = {
    val result = for {
      lookupResult <- EitherT(companiesDAO.lookup(id))
      _            <- EitherT(Future(authorizationMethods.canGet(request, lookupResult)))
    } yield lookupResult
    result.value
  }

  def all(request: Request[AnyContent]): Future[Seq[Company]] = {
    val result = for {
      lookupResult <- EitherT(companiesDAO.all().map(_.asRight[ErrorStatus]))
    } yield lookupResult
    result.value.completeOrThrow
  }

  def patch(company: Company, request: RequestWithCompany[AnyContent]): Future[Result[Company]] = {
    val result = for {
      oldCompany    <- EitherT(companiesDAO.lookup(company.id.getOrElse(UUID.randomUUID())))
      _             <- EitherT(Future(authorizationMethods.canUpdate(request, company)))
      companyUpdate <- EitherT(companiesDAO.update(company.copy(id = oldCompany.id)))
    } yield companyUpdate
    result.value
  }
  def post(company: Company, request: RequestWithCompany[AnyContent]): Future[Result[Company]] = {
    val result: EitherT[Future, ErrorStatus, Company] = for {
      _             <- EitherT(Future(authorizationMethods.canCreate(request, company)))
      createdResult <- EitherT(companiesDAO.create(company))
    } yield createdResult
    result.value
  }
  def delete(id: UUID, request: RequestWithCompany[AnyContent]): Future[Result[Company]] = {
    val result: EitherT[Future, ErrorStatus, Company] = for {
      company <- EitherT(companiesDAO.lookup(id))
      _       <- EitherT(Future(authorizationMethods.canDelete(request, company)))
      _       <- EitherT(companiesDAO.delete(id))
    } yield company
    result.value
  }
}

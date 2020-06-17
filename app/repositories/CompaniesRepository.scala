package repositories

import java.util.UUID

import actions.RequestWithCompany
import cats.implicits._
import cats.data.EitherT
import common.authorization.CompanyAuthorizationMethods
import common.results.Results.{ ErrorStatus, Result }
import db.CompaniesDAO
import javax.inject.{ Inject, Singleton }
import models.api.Company
import play.api.mvc.AnyContent

import scala.concurrent.{ ExecutionContext, Future }

trait CompaniesRepository {
  def lookup(id: UUID, request: RequestWithCompany[AnyContent]): Future[Result[Company]]
  def patch(company: Company, request: RequestWithCompany[AnyContent]): Future[Result[Company]]
  def post(company: Company, request: RequestWithCompany[AnyContent]): Future[Result[Company]]
  def delete(id: UUID, request: RequestWithCompany[AnyContent]): Future[Result[Company]]
}

class CompaniesRepositoryImpl @Inject()(
  companiesDAO: CompaniesDAO,
  authorizationMethods: CompanyAuthorizationMethods[AnyContent]
)(implicit ec: ExecutionContext)
    extends CompaniesRepository {

  def lookup(id: UUID, request: RequestWithCompany[AnyContent]): Future[Result[Company]] = {
    val result = for {
      lookupResult <- EitherT(companiesDAO.lookup(id))
      _            <- EitherT(Future(authorizationMethods.canGet(request, lookupResult)))
    } yield lookupResult
    result.value
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

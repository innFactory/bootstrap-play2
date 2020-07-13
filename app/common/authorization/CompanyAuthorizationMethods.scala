package common.authorization
import actions.RequestWithCompany
import com.google.inject.Inject
import common.results.Results.{ ErrorStatus, Result }
import common.results.errors.Errors.{ BadRequest, Forbidden }
import common.utils.{ CompanyIdEqualsId, OptionAndCompanyId }
import models.api.Company
import play.api.mvc.BodyParsers
import play.api.Configuration

import scala.concurrent.ExecutionContext

/**
 * *
 * Auth Methods for Owner endpoint
 */
class CompanyAuthorizationMethods[A] @Inject() (
  val parser: BodyParsers.Default
)(implicit val executionContext: ExecutionContext, configuration: Configuration) {

  def canGet(request: RequestWithCompany[A], company: Company): Either[ErrorStatus, Boolean] =
    OptionAndCompanyId(request.company, company.id.get) match {
      case CompanyIdEqualsId() => Right(true)
      case _                   => Left(Forbidden())
    }

  // Everyone can create owners
  def canCreate(request: RequestWithCompany[A], company: Company): Result[Boolean] =
    request.company match {
      case Some(_)                                                                                       => Left(BadRequest())
      case None if company.firebaseUser.getOrElse(List.empty).contains(request.email.getOrElse("empty")) => Right(true)
      case _                                                                                             => Left(Forbidden())
    }

  def canDelete(request: RequestWithCompany[A], company: Company): Result[Boolean] =
    OptionAndCompanyId(request.company, company.id.get) match {
      case CompanyIdEqualsId() => Right(true)
      case _                   => Left(Forbidden())
    }

  def canUpdate(request: RequestWithCompany[A], company: Company): Result[Boolean] =
    OptionAndCompanyId(request.company, company.id.get) match {
      case CompanyIdEqualsId() => Right(true)
      case _                   => Left(Forbidden())
    }

}

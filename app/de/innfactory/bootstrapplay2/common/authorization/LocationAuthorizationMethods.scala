package de.innfactory.bootstrapplay2.common.authorization

import java.util.UUID

import de.innfactory.bootstrapplay2.actions.RequestWithCompany
import com.google.inject.Inject
import de.innfactory.bootstrapplay2.common.results.Results.Result
import de.innfactory.bootstrapplay2.common.results.errors.Errors.Forbidden
import de.innfactory.bootstrapplay2.models.api.{ Company, Location }
import play.api.mvc.{ BodyParsers, Request }
import play.api.Configuration
import de.innfactory.bootstrapplay2.common.utils.{
  CompanyAndLocation,
  CompanyCompanyIdAndOldCompanyId,
  CompanyIdEqualsId,
  CompanyIdsAreEqual,
  IsCompanyOfLocation,
  OptionAndCompanyId
}

import scala.concurrent.{ ExecutionContext, Future }

/**
 * *
 * Auth Methods for Locations endpoint
 */
class LocationAuthorizationMethods[A] @Inject() (
  val parser: BodyParsers.Default
)(implicit
  val executionContext: ExecutionContext,
  configuration: Configuration,
  firebaseEmailExtractor: FirebaseEmailExtractor[Request[Any]]
) {

  def accessGet(request: RequestWithCompany[A], location: Location): Result[Boolean] = {
    val companyOption: Option[Company] = request.company
    CompanyAndLocation(companyOption, location) match {
      case IsCompanyOfLocation() => Right(true)
      case _                     => Left(Forbidden())
    }
  }

  def accessGetAllByCompany(id: UUID, request: RequestWithCompany[A]): Result[Boolean] = {
    val companyOption: Option[Company] = request.company
    OptionAndCompanyId(companyOption, id) match {
      case CompanyIdEqualsId() => Right(true)
      case _                   => Left(Forbidden())
    }
  }

  def update(request: RequestWithCompany[A], ownerId: UUID, oldOwnerId: UUID): Result[Boolean] = {
    val companyOption: Option[Company] = request.company
    CompanyCompanyIdAndOldCompanyId(companyOption, ownerId, oldOwnerId) match {
      case CompanyIdsAreEqual() => Right(true)
      case _                    => Left(Forbidden())
    }
  }

  def createDelete(request: RequestWithCompany[A], locationOwnerId: UUID): Future[Result[Boolean]] = {
    val result = for {
      company <- request.company
    } yield
      if (company.id.get.equals(locationOwnerId))
        Right(true)
      else
        Left(Forbidden())
    Future(result.getOrElse(Left(Forbidden())))
  }

}

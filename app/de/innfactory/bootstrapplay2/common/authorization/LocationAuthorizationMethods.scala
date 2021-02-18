package de.innfactory.bootstrapplay2.common.authorization

import java.util.UUID
import com.google.inject.Inject
import de.innfactory.bootstrapplay2.common.request.RequestContextWithCompany
import de.innfactory.bootstrapplay2.common.results.Results.Result
import de.innfactory.bootstrapplay2.common.results.errors.Errors.Forbidden
import de.innfactory.bootstrapplay2.models.api.{ Company, Location }
import play.api.mvc.{ BodyParsers, Request }
import play.api.Configuration
import de.innfactory.bootstrapplay2.common.utils.{
  CompanyAndLocation,
  CompanyId,
  CompanyIdAndOldCompanyId,
  CompanyIdEqualsId,
  CompanyIdsAreEqual,
  IsCompanyOfLocation
}
import de.innfactory.implicits.BooleanImplicits.EnhancedBoolean

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

  def accessGet(location: Location)(implicit rc: RequestContextWithCompany): Result[Boolean] =
    CompanyAndLocation(rc.company, location) match {
      case IsCompanyOfLocation() => Right(true)
      case _                     => Left(Forbidden())
    }

  def accessGetAllByCompany(id: UUID)(implicit rc: RequestContextWithCompany): Result[Boolean] =
    CompanyId(rc.company, id) match {
      case CompanyIdEqualsId() => Right(true)
      case _                   => Left(Forbidden())
    }

  def update(ownerId: UUID, oldOwnerId: UUID)(implicit rc: RequestContextWithCompany): Result[Boolean] =
    CompanyIdAndOldCompanyId(rc.company, ownerId, oldOwnerId) match {
      case CompanyIdsAreEqual() => Right(true)
      case _                    => Left(Forbidden())
    }

  def createDelete(locationOwnerId: UUID)(implicit rc: RequestContextWithCompany): Result[Boolean] =
    rc.company.id.get.equals(locationOwnerId).toResult(Forbidden())

}

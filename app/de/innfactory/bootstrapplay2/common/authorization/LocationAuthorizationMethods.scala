package de.innfactory.bootstrapplay2.common.authorization

import de.innfactory.bootstrapplay2.common.request.RequestContextWithUser
import de.innfactory.bootstrapplay2.common.results.Results.Result
import de.innfactory.bootstrapplay2.common.results.errors.Errors.Forbidden
import de.innfactory.bootstrapplay2.models.api.Location
import de.innfactory.implicits.BooleanImplicits.EnhancedBoolean
import de.innfactory.bootstrapplay2.services.firebase.utils.Utils._

object LocationAuthorizationMethods {
  def accessGet(location: Location)(implicit rc: RequestContextWithUser): Result[Boolean] =
    rc.user.isCompanyAdmin(location.company).toResult(Forbidden())

  def accessGetAllByCompany(companyId: Long)(implicit rc: RequestContextWithUser): Result[Boolean] =
    rc.user.isCompanyAdmin(companyId).toResult(Forbidden())

  def update(ownerId: Long, oldOwnerId: Long)(implicit rc: RequestContextWithUser): Result[Boolean] =
    (rc.user.isCompanyAdmin(ownerId) && ownerId == oldOwnerId).toResult(Forbidden())

  def createDelete(locationOwnerId: Long)(implicit rc: RequestContextWithUser): Result[Boolean] =
    rc.user.isCompanyAdmin(locationOwnerId).toResult(Forbidden())
}

package de.innfactory.bootstrapplay2.common.authorization
import de.innfactory.bootstrapplay2.common.request.RequestContextWithUser
import de.innfactory.bootstrapplay2.common.results.Results.{ Result, ResultStatus }
import de.innfactory.bootstrapplay2.common.results.errors.Errors.Forbidden
import de.innfactory.bootstrapplay2.models.api.Company
import de.innfactory.bootstrapplay2.services.firebase.utils.Utils._
import de.innfactory.implicits.BooleanImplicits.EnhancedBoolean

object CompanyAuthorizationMethods {

  def canGet(company: Company)(implicit rc: RequestContextWithUser): Either[ResultStatus, Boolean] =
    rc.user.isCompanyAdmin(company.id.get).toResult(Forbidden())

  def canCreate()(implicit rc: RequestContextWithUser): Result[Boolean] =
    rc.user.isInnFactoryAdmin.toResult(Forbidden())

  def canDelete(company: Company)(implicit rc: RequestContextWithUser): Result[Boolean] =
    rc.user.isCompanyAdmin(company.id.get).toResult(Forbidden())

  def canUpdate(company: Company)(implicit rc: RequestContextWithUser): Result[Boolean] =
    rc.user.isCompanyAdmin(company.id.get).toResult(Forbidden())

}

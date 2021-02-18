package de.innfactory.bootstrapplay2.common.authorization
import com.google.inject.Inject
import de.innfactory.bootstrapplay2.common.request.{ RequestContext, RequestContextWithCompany }
import de.innfactory.bootstrapplay2.common.results.Results.{ Result, ResultStatus }
import de.innfactory.bootstrapplay2.common.results.errors.Errors.{ BadRequest, Forbidden }
import de.innfactory.bootstrapplay2.models.api.Company
import de.innfactory.implicits.BooleanImplicits.EnhancedBoolean
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

  def canGet(company: Company)(implicit rc: RequestContextWithCompany): Either[ResultStatus, Boolean] =
    company.id.get.equals(rc.company.id.get).toResult(Forbidden())

  // Everyone can create owners
  def canCreate(company: Company)(implicit rc: RequestContext): Result[Boolean] =
    Right(true)

  def canDelete(company: Company)(implicit rc: RequestContextWithCompany): Result[Boolean] =
    company.id.get.equals(rc.company.id.get).toResult(Forbidden())

  def canUpdate(company: Company)(implicit rc: RequestContextWithCompany): Result[Boolean] =
    company.id.get.equals(rc.company.id.get).toResult(Forbidden())

}

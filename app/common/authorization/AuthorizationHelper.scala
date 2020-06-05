package common.authorization

import java.util.UUID

import com.google.inject.Inject
import models.api.{ Company, Location }
import db.LocationsDAO
import play.api.mvc.BodyParsers

import scala.concurrent.{ ExecutionContext, Future }

/***
 * Auth Helper Methods
 */
class AuthorizationHelper[A] @Inject()(
  val parser: BodyParsers.Default,
)(implicit val executionContext: ExecutionContext, locationsDAO: LocationsDAO) {

  def extractLocationsFromOwner(company: Company): Future[Seq[Location]] =
    locationsDAO.lookupByCompany(company.id.getOrElse(UUID.randomUUID())).map {
      case Right(success: Seq[Location]) =>
        success
      case _ =>
        Seq.empty
    }

}

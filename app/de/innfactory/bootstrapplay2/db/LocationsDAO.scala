package de.innfactory.bootstrapplay2.db
import com.google.inject.ImplementedBy
import com.vividsolutions.jts.geom.Geometry
import de.innfactory.common.geo.GeoPointFactory
import de.innfactory.bootstrapplay2.common.request.RequestContext
import de.innfactory.bootstrapplay2.common.results.Results.Result
import de.innfactory.bootstrapplay2.common.results.errors.Errors.{ DatabaseResult, NotFound }
import de.innfactory.play.db.codegen.XPostgresProfile

import javax.inject.{ Inject, Singleton }
import slick.jdbc.JdbcBackend.Database
import play.api.libs.json.Json
import de.innfactory.bootstrapplay2.models.api.{ Location => LocationObject }
import org.joda.time.DateTime
import de.innfactory.bootstrapplay2.models.api.Location.patch

import scala.concurrent.{ ExecutionContext, Future }
import scala.language.implicitConversions

@ImplementedBy(classOf[SlickLocationsDAO])
trait LocationsDAO {

  def lookup(id: Long)(implicit rc: RequestContext): Future[Result[LocationObject]]

  def lookupByCompany(
    companyId: Long
  )(implicit rc: RequestContext): Future[Result[Seq[LocationObject]]]

  def allFromDistanceByCompany(
    companyId: Long,
    point: Geometry,
    distance: Long
  )(implicit rc: RequestContext): Future[Result[Seq[LocationObject]]]

  def create(locationObject: LocationObject)(implicit rc: RequestContext): Future[Result[LocationObject]]

  def update(locationObject: LocationObject)(implicit rc: RequestContext): Future[Result[LocationObject]]

  def delete(id: Long)(implicit rc: RequestContext): Future[Result[Boolean]]

  def close(): Future[Unit]
}

@Singleton
class SlickLocationsDAO @Inject() (db: Database)(implicit ec: ExecutionContext)
    extends BaseSlickDAO(db)
    with LocationsDAO {

  override val profile = XPostgresProfile
  import profile.api._

  /* - - - Compiled Queries - - - */

  private val queryById = Compiled((id: Rep[Long]) => Location.filter(_.id === id))

  private val queryByCompany = Compiled((id: Rep[Long]) => Location.filter(_.company === id))

  private val querySortedWithDistanceFilterMaxDistance =
    Compiled((ref: Rep[Geometry], maxDistance: Rep[Float], companyId: Rep[Long]) =>
      Location
        .filter(_.company === companyId)
        .sortBy(_.position.distanceSphere(ref))
        .map { x =>
          (x, x.position.distanceSphere(ref))
        }
        .filter(_._2 <= maxDistance)
    )

  def lookup(id: Long)(implicit rc: RequestContext): Future[Result[LocationObject]] =
    lookupGeneric[LocationRow, LocationObject](
      queryById(id).result.headOption
    )

  def _internal_lookup(id: Long)(implicit rc: RequestContext): Future[Option[LocationObject]] =
    db.run(queryById(id).result.headOption).map {
      case Some(row) =>
        Some(locationRowToLocation(row))
      case None      =>
        None
    }

  def lookupByCompany(
    companyId: Long
  )(implicit rc: RequestContext): Future[Result[Seq[LocationObject]]] =
    lookupSequenceGeneric[LocationRow, LocationObject](
      queryByCompany(companyId).result
    )

  def allFromDistanceByCompany(
    companyId: Long,
    point: Geometry,
    distance: Long
  )(implicit rc: RequestContext): Future[Result[Seq[LocationObject]]] =
    db.run(querySortedWithDistanceFilterMaxDistance(point, distance.toFloat, companyId).result).map { seq =>
      Right(
        seq.map(x => locationRowToLocationWithDistance(x._1, x._2))
      )
    }

  def update(locationObject: LocationObject)(implicit rc: RequestContext): Future[Result[LocationObject]] =
    updateGeneric[LocationRow, LocationObject](
      queryById(locationObject.id.getOrElse(0)).result.headOption,
      (toPatch: LocationObject) =>
        queryById(locationObject.id.getOrElse(0)).update(locationObjectToLocationRow(toPatch)),
      (old: LocationObject) => patch(locationObject, old)
    )

  def delete(id: Long)(implicit rc: RequestContext): Future[Result[Boolean]] =
    deleteGeneric[LocationRow, LocationObject](
      queryById(id).result.headOption,
      queryById(id).delete
    )

  def create(locationObject: LocationObject)(implicit rc: RequestContext): Future[Result[LocationObject]] =
    createGeneric[LocationRow, LocationObject](
      locationObject,
      queryById(locationObject.id.getOrElse(0)).result.headOption,
      (entityToSave: LocationRow) => (Location returning Location) += entityToSave
    )

  implicit private def locationObjectToLocationRow(locationObject: LocationObject): LocationRow =
    LocationRow(
      id = locationObject.id.getOrElse(0),
      company = locationObject.company,
      name = locationObject.name.getOrElse(""),
      settings = locationObject.settings.getOrElse(Json.parse("{}")),
      position = GeoPointFactory.createPoint(locationObject.lon.getOrElse(0), locationObject.lat.getOrElse(0)),
      addressLine1 = locationObject.addressLine1.getOrElse(""),
      addressLine2 = locationObject.addressLine2.getOrElse(""),
      zip = locationObject.zip.getOrElse(""),
      city = locationObject.city.getOrElse(""),
      country = locationObject.country.getOrElse(""),
      created = locationObject.created.getOrElse(DateTime.now),
      updated = locationObject.updated.getOrElse(DateTime.now)
    )

  implicit private def locationRowToLocation(locationRow: LocationRow): LocationObject =
    LocationObject(
      id = Some(locationRow.id),
      company = locationRow.company,
      name = Some(locationRow.name),
      settings = Some(locationRow.settings),
      lon = Some(locationRow.position.getCoordinate.x),
      lat = Some(locationRow.position.getCoordinate.y),
      addressLine1 = Some(locationRow.addressLine1),
      addressLine2 = Some(locationRow.addressLine2),
      zip = Some(locationRow.zip),
      city = Some(locationRow.city),
      country = Some(locationRow.country),
      created = Some(locationRow.created),
      updated = Some(locationRow.updated),
      distance = None
    )

  implicit private def locationRowToLocationWithDistance(locationRow: LocationRow, distance: Float): LocationObject =
    LocationObject(
      id = Some(locationRow.id),
      company = locationRow.company,
      name = Some(locationRow.name),
      settings = Some(locationRow.settings),
      lon = Some(locationRow.position.getCoordinate.x),
      lat = Some(locationRow.position.getCoordinate.y),
      addressLine1 = Some(locationRow.addressLine1),
      addressLine2 = Some(locationRow.addressLine2),
      zip = Some(locationRow.zip),
      city = Some(locationRow.city),
      country = Some(locationRow.country),
      created = Some(locationRow.created),
      updated = Some(locationRow.updated),
      distance = Some(distance)
    )

}

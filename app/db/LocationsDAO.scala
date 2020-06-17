package db

import java.util.UUID

import com.vividsolutions.jts.geom.Geometry
import common.GeoPointFactory.GeoPointFactory
import common.daos.BaseSlickDAO
import common.results.Results.Result
import common.results.errors.Errors.{ DatabaseError, NotFound }
import db.codegen.XPostgresProfile
import javax.inject.{ Inject, Singleton }
import slick.jdbc.JdbcBackend.Database
import play.api.libs.json.Json
import models.api.{ Location => LocationObject }
import dbdata.Tables
import org.joda.time.DateTime
import models.api.Location.patch

import scala.concurrent.{ ExecutionContext, Future }
import scala.language.implicitConversions

/**
 * An implementation dependent DAO.  This could be implemented by Slick, Cassandra, or a REST API.
 */
trait LocationsDAO {

  def lookup(id: Long): Future[Result[LocationObject]]

  def lookupByCompany(
    companyId: UUID
  ): Future[Result[Seq[LocationObject]]]

  def allFromDistanceByCompany(
    companyId: UUID,
    point: Geometry,
    distance: Long
  ): Future[Result[Seq[LocationObject]]]

  def create(locationObject: LocationObject): Future[Result[LocationObject]]

  def update(locationObject: LocationObject): Future[Result[LocationObject]]

  def delete(id: Long): Future[Result[Boolean]]

  def close(): Future[Unit]
}

/**
 * A locationObject DAO implemented with Slick, leveraging Slick code gen.
 *
 * Note that you must run "flyway/flywayMigrate" before "compile" here.
 *
 * @param db the slick database that this locationObject DAO is using internally, bound through Module.
 * @param ec a CPU bound execution context.  Slick manages blocking JDBC calls with its
 *    own internal thread pool, so Play's default execution context is fine here.
 */
@Singleton
class SlickLocationsDAO @Inject()(db: Database)(implicit ec: ExecutionContext)
    extends BaseSlickDAO(db)
    with LocationsDAO {

  // Class Name for identification in Database Errors
  override val currentClassForDatabaseError = "SlickLocationsDAO"

  override val profile = XPostgresProfile
  import profile.api._

  /* - - - Compiled Queries - - - */

  private val queryById = Compiled((id: Rep[Long]) => Location.filter(_.id === id))

  private val queryByCompany = Compiled((id: Rep[UUID]) => Location.filter(_.company === id))

  private val querySortedWithDistanceFilterMaxDistance = Compiled(
    (ref: Rep[Geometry], maxDistance: Rep[Float], companyId: Rep[UUID]) =>
      Location
        .filter(_.company === companyId)
        .sortBy(_.position.distanceSphere(ref))
        .map(x => {
          (x, x.position.distanceSphere(ref))
        })
        .filter(_._2 <= maxDistance)
  )

  /**
   * Lookup single object
   * @param id
   * @return
   */
  def lookup(id: Long): Future[Result[LocationObject]] =
    lookupGeneric[LocationRow, LocationObject](
      queryById(id).result.headOption
    )

  /**
   * Lookup single object _internal use only
   * @param id
   * @return
   */
  def _internal_lookup(id: Long): Future[Option[LocationObject]] =
    db.run(queryById(id).result.headOption).map {
      case Some(row) =>
        Some(locationRowToLocation(row))
      case None =>
        None
    }

  /**
   * Lookup by Company
   * @param companyId
   * @return
   */
  def lookupByCompany(
    companyId: UUID,
  ): Future[Result[Seq[LocationObject]]] =
    lookupSequenceGeneric[LocationRow, LocationObject](
      queryByCompany(companyId).result
    )

  /**
   * Query All by distance from to index

   * @param point
   * @param distance
   * @return
   */
  def allFromDistanceByCompany(
    companyId: UUID,
    point: Geometry,
    distance: Long
  ): Future[Result[Seq[LocationObject]]] =
    db.run(querySortedWithDistanceFilterMaxDistance(point, distance.toFloat, companyId).result)
      .map(seq => {
        Right(
          seq.map(x => locationRowToLocationWithDistance(x._1, x._2))
        )
      })

  /**
   * Patch object
   * @param locationObject
   * @return
   */
  def update(locationObject: LocationObject): Future[Result[LocationObject]] =
    updateGeneric[LocationRow, LocationObject](
      queryById(locationObject.id.getOrElse(0)).result.headOption,
      (toPatch: LocationObject) =>
        queryById(locationObject.id.getOrElse(0)).update(locationObjectToLocationRow(toPatch)),
      (old: LocationObject) => patch(locationObject, old)
    )

  /**
   * Delete Object
   * @param id
   * @return
   */
  def delete(id: Long): Future[Result[Boolean]] =
    deleteGeneric[LocationRow, LocationObject](
      queryById(id).result.headOption,
      queryById(id).delete
    )

  /**
   * Create new Object
   * @param locationObject
   * @return
   */
  def create(locationObject: LocationObject): Future[Result[LocationObject]] =
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

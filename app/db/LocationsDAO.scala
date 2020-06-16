package db

import java.util.UUID

import com.vividsolutions.jts.geom.Geometry
import common.GeoPointFactory.GeoPointFactory
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

  def all(from: Int, to: Int): Future[Result[Seq[LocationObject]]]

  def allFromDistance(
    from: Int,
    to: Int,
    point: Geometry,
    distance: Long
  ): Future[Result[Seq[LocationObject]]]

  def allFromDistancePublic(
    point: Geometry,
    distance: Long
  ): Future[Seq[LocationObject]]

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
class SlickLocationsDAO @Inject()(db: Database)(implicit ec: ExecutionContext) extends LocationsDAO with Tables {

  override val profile = XPostgresProfile

  import profile.api._

  private val slickLocation = "SlickLocationsDAO"

  private val queryById = Compiled((id: Rep[Long]) => Location.filter(_.id === id))

  private val queryByCompany = Compiled((id: Rep[UUID]) => Location.filter(_.company === id))

  private val querySortedWithDistanceFilterMaxDistance = Compiled(
    (ref: Rep[Geometry], maxDistance: Rep[Float]) =>
      Location
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
    db.run(queryById(id).result.headOption).map {
      case Some(row) =>
        Right(locationRowToLocation(row))
      case None =>
        Left(
          NotFound()
        )
    }

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
  ): Future[Result[Seq[LocationObject]]] = {
    val query = queryByCompany(companyId).result
    val f     = db.run(query)
    f.map(seq => {
      Right(seq.map(locationRowToLocation))
    })
  }

  /**
   * All objects from(index) to(index)
   * @param from
   * @param to
   * @return
   */
  def all(
    from: Int,
    to: Int,
  ): Future[Result[Seq[LocationObject]]] =
    db.run(Location.result)
      .map(seq => {
        Right(seq.slice(from, to + 1).map(locationRowToLocation))
      })

  /**
   * Query All by distance from to index
   * @param from
   * @param to
   * @param point
   * @param distance
   * @return
   */
  def allFromDistance(
    from: Int,
    to: Int,
    point: Geometry,
    distance: Long
  ): Future[Result[Seq[LocationObject]]] = {

    val query =
      querySortedWithDistanceFilterMaxDistance(point, distance.toFloat).result
    val f = db.run(query)
    f.map(seq => {
      Right(
        seq
          .slice(from, to + 1)
          .map(x => locationRowToLocationWithDistance(x._1, x._2))
      )

    })
  }

  /**
   * Get total object count by distance public
   * @param point
   * @param distance
   * @return
   */
  def getCountByDistancePublic(point: Geometry, distance: Float): Future[Int] = {
    val query = querySortedWithDistanceFilterMaxDistance(point, distance).result
    val l     = db.run(query)
    val lengthMap: Future[Int] = l.map(s => {
      s.length
    })
    lengthMap
  }

  /**
   * Query All by distance for public
   * @param point
   * @param distance
   * @return
   */
  def allFromDistancePublic(
    point: Geometry,
    distance: Long
  ): Future[Seq[LocationObject]] = {

    val query =
      querySortedWithDistanceFilterMaxDistance(point, distance.toFloat).result
    val f = db.run(query)
    f.map(seq => {
      seq.map(x => locationRowToLocationWithDistance(x._1, x._2))
    })
  }

  /**
   * Patch object
   * @param locationObject
   * @return
   */
  def update(locationObject: LocationObject): Future[Result[LocationObject]] =
    (db
      .run(queryById(locationObject.id.getOrElse(0)).result.headOption))
      .map {
        case Some(option: LocationRow) => {
          val oldObject     = locationRowToLocation(option)
          val patchedObject = patch(locationObject, oldObject)
          db.run(
              queryById(locationObject.id.getOrElse(0))
                .update(locationObjectToLocationRow(patchedObject))
            )
            .map {
              case 0 =>
                Left(
                  DatabaseError("Could not replace entity", slickLocation, "update", "row not updated")
                )
              case _ => Right(patchedObject)
            }
        }
        case None =>
          Future(
            Left(
              DatabaseError("Could not find entity to update", slickLocation, "update", "entity not found")
            )
          )
      }
      .flatten

  /**
   * Delete Object
   * @param id
   * @return
   */
  def delete(id: Long): Future[Result[Boolean]] = {
    for {
      dbQueryResult <- db.run(queryById(id).result.headOption)
    } yield
      for {
        dbDeleteResult <- db.run(queryById(id).delete)
      } yield {
        dbDeleteResult
      } match {
        case 0 =>
          Left(
            DatabaseError("could not delete entity", slickLocation, "delete", "entity was deleted")
          )
        case _ => Right(true)
      }
  }.flatten

  /**
   * Create new Object
   * @param locationObject
   * @return
   */
  def create(locationObject: LocationObject): Future[Result[LocationObject]] =
    try {
      val entityToSave = locationObjectToLocationRow(locationObject)
      val action       = (Location returning Location) += entityToSave
      val actionResult = db.run(action)
      actionResult.map { createdObject =>
        Right(locationRowToLocation(createdObject))
      }
    } catch {
      case _: Throwable =>
        Future(
          Left(
            DatabaseError("failed to create", slickLocation, "create", "could not create entity")
          )
        )
    }

  private def locationObjectToLocationRow(locationObject: LocationObject): LocationRow =
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

  private def locationRowToLocation(locationRow: LocationRow): LocationObject =
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

  private def locationRowToLocationWithDistance(locationRow: LocationRow, distance: Float): LocationObject =
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

  /**
   * Close db
   * @return
   */
  def close(): Future[Unit] =
    Future.successful(db.close())

}

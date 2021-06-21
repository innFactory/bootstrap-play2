package de.innfactory.bootstrapplay2.repositories

import java.util.UUID
import cats.data.EitherT
import de.innfactory.bootstrapplay2.common.authorization.LocationAuthorizationMethods
import de.innfactory.bootstrapplay2.common.results.Results.{ Result, ResultStatus }
import javax.inject.Inject
import de.innfactory.bootstrapplay2.models.api.Location
import cats.implicits._
import com.google.inject.ImplementedBy
import de.innfactory.bootstrapplay2.common.logging.ImplicitLogContext
import de.innfactory.bootstrapplay2.common.repositories.{ Delete, Lookup, Patch, Post }
import de.innfactory.bootstrapplay2.common.request.RequestContextWithUser
import de.innfactory.common.geo.GeoPointFactory
import de.innfactory.bootstrapplay2.services.firebase.utils.Utils._
import de.innfactory.bootstrapplay2.db.LocationsDAO

import scala.concurrent.{ ExecutionContext, Future }

@ImplementedBy(classOf[LocationRepositoryImpl])
trait LocationRepository
    extends Lookup[Long, RequestContextWithUser, Location]
    with Patch[RequestContextWithUser, Location]
    with Post[RequestContextWithUser, Location]
    with Delete[Long, RequestContextWithUser, Location] {
  def getByDistance(
    distance: Long,
    lat: Double,
    lon: Double
  )(implicit rc: RequestContextWithUser): Future[Result[Seq[Location]]]
  def lookupByCompany(id: Long)(implicit rc: RequestContextWithUser): Future[Result[Seq[Location]]]
}

class LocationRepositoryImpl @Inject() (
  locationsDAO: LocationsDAO
)(implicit ec: ExecutionContext)
    extends LocationRepository
    with ImplicitLogContext {
  override def lookup(id: Long)(implicit rc: RequestContextWithUser): Future[Result[Location]] = {
    val result = for {
      lookupResult <- EitherT(locationsDAO.lookup(id))
      _            <- EitherT(Future(LocationAuthorizationMethods.accessGet(lookupResult)))
    } yield lookupResult
    result.value
  }

  override def getByDistance(
    distance: Long,
    lat: Double,
    lon: Double
  )(implicit rc: RequestContextWithUser): Future[Result[Seq[Location]]] = {
    val geometryPoint = GeoPointFactory.createPoint(lat, lon)
    val result        = for {
      company      <- EitherT(Future(rc.user.getCompanyId()))
      _            <- EitherT(
                        Future(LocationAuthorizationMethods.accessGetAllByCompany(company))
                      )
      lookupResult <- EitherT(
                        locationsDAO
                          .allFromDistanceByCompany(company, geometryPoint, distance)
                      )
    } yield lookupResult
    result.value
  }

  def lookupByCompany(id: Long)(implicit rc: RequestContextWithUser): Future[Result[Seq[Location]]] = {
    val result = for {
      _            <- EitherT(Future(LocationAuthorizationMethods.accessGetAllByCompany(id)))
      lookupResult <- EitherT(locationsDAO.lookupByCompany(id))
    } yield lookupResult
    result.value
  }

  def patch(location: Location)(implicit rc: RequestContextWithUser): Future[Result[Location]] = {
    val result = for {
      oldLocation    <- EitherT(locationsDAO.lookup(location.id.getOrElse(0)))
      _              <- EitherT(Future(LocationAuthorizationMethods.update(location.company, oldLocation.company)))
      locationUpdate <- EitherT(locationsDAO.update(location.copy(id = oldLocation.id)))
    } yield locationUpdate
    result.value
  }

  def post(location: Location)(implicit rc: RequestContextWithUser): Future[Result[Location]] = {
    val result: EitherT[Future, ResultStatus, Location] = for {
      _             <- EitherT(Future(LocationAuthorizationMethods.createDelete(location.company)))
      createdResult <- EitherT(locationsDAO.create(location))
    } yield createdResult
    result.value
  }

  def delete(id: Long)(implicit rc: RequestContextWithUser): Future[Result[Location]] = {
    val result: EitherT[Future, ResultStatus, Location] = for {
      location <- EitherT(locationsDAO.lookup(id))
      _        <- EitherT(Future(LocationAuthorizationMethods.createDelete(location.company)))
      _        <- EitherT(locationsDAO.delete(location.id.get))
    } yield location
    result.value
  }
}

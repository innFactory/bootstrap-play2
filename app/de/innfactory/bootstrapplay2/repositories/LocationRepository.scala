package de.innfactory.bootstrapplay2.repositories

import java.util.UUID

import de.innfactory.bootstrapplay2.actions.RequestWithCompany
import cats.data.EitherT
import de.innfactory.bootstrapplay2.common.authorization.LocationAuthorizationMethods
import de.innfactory.bootstrapplay2.common.results.Results.{ ErrorStatus, Result }
import javax.inject.Inject
import de.innfactory.bootstrapplay2.models.api.Location
import cats.implicits._
import com.google.inject.ImplementedBy
import de.innfactory.common.geo.GeoPointFactory
import de.innfactory.bootstrapplay2.common.results.errors.Errors.Forbidden
import play.api.mvc.AnyContent
import de.innfactory.bootstrapplay2.common.utils.OptionUtils._
import de.innfactory.bootstrapplay2.db.LocationsDAO

import scala.concurrent.{ ExecutionContext, Future }

@ImplementedBy(classOf[LocationRepositoryImpl])
trait LocationRepository {
  def lookup(id: Long, request: RequestWithCompany[AnyContent]): Future[Result[Location]]
  def getByDistance(
    distance: Long,
    lat: Double,
    lon: Double,
    request: RequestWithCompany[AnyContent]
  ): Future[Result[Seq[Location]]]
  def lookupByCompany(id: UUID, request: RequestWithCompany[AnyContent]): Future[Result[Seq[Location]]]
  def patch(location: Location, request: RequestWithCompany[AnyContent]): Future[Result[Location]]
  def post(location: Location, request: RequestWithCompany[AnyContent]): Future[Result[Location]]
  def delete(id: Long, request: RequestWithCompany[AnyContent]): Future[Result[Location]]
}

class LocationRepositoryImpl @Inject() (
  locationsDAO: LocationsDAO,
  authorizationMethods: LocationAuthorizationMethods[AnyContent]
)(implicit ec: ExecutionContext)
    extends LocationRepository {
  override def lookup(id: Long, request: RequestWithCompany[AnyContent]): Future[Result[Location]] = {
    val result = for {
      lookupResult <- EitherT(locationsDAO.lookup(id))
      _            <- EitherT(Future(authorizationMethods.accessGet(request, lookupResult)))
    } yield lookupResult
    result.value
  }

  override def getByDistance(
    distance: Long,
    lat: Double,
    lon: Double,
    request: RequestWithCompany[AnyContent]
  ): Future[Result[Seq[Location]]] = {
    val geometryPoint = GeoPointFactory.createPoint(lat, lon)
    val result        = for {
      company      <- EitherT(Future(request.company.toEither(Forbidden())))
      _            <- EitherT(Future(authorizationMethods.accessGetAllByCompany(company.id.getOrElse(UUID.randomUUID()), request)))
      lookupResult <- EitherT(
                        locationsDAO
                          .allFromDistanceByCompany(company.id.getOrElse(UUID.randomUUID()), geometryPoint, distance)
                      )
    } yield lookupResult
    result.value
  }

  def lookupByCompany(id: UUID, request: RequestWithCompany[AnyContent]): Future[Result[Seq[Location]]] = {
    val result = for {
      _            <- EitherT(Future(authorizationMethods.accessGetAllByCompany(id, request)))
      lookupResult <- EitherT(locationsDAO.lookupByCompany(id))
    } yield lookupResult
    result.value
  }

  def patch(location: Location, request: RequestWithCompany[AnyContent]): Future[Result[Location]] = {
    val result = for {
      oldLocation    <- EitherT(locationsDAO.lookup(location.id.getOrElse(0)))
      _              <- EitherT(Future(authorizationMethods.update(request, location.company, oldLocation.company)))
      locationUpdate <- EitherT(locationsDAO.update(location.copy(id = oldLocation.id)))
    } yield locationUpdate
    result.value
  }

  def post(location: Location, request: RequestWithCompany[AnyContent]): Future[Result[Location]] = {
    val result: EitherT[Future, ErrorStatus, Location] = for {
      _             <- EitherT(authorizationMethods.createDelete(request, location.company))
      createdResult <- EitherT(locationsDAO.create(location))
    } yield createdResult
    result.value
  }

  def delete(id: Long, request: RequestWithCompany[AnyContent]): Future[Result[Location]] = {
    val result: EitherT[Future, ErrorStatus, Location] = for {
      location <- EitherT(locationsDAO.lookup(id))
      _        <- EitherT(authorizationMethods.createDelete(request, location.company))
      _        <- EitherT(locationsDAO.delete(location.id.get))
    } yield location
    result.value
  }
}
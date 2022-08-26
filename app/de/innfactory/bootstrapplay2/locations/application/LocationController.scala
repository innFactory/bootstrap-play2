package de.innfactory.bootstrapplay2.locations.application

import akka.stream.Materializer
import cats.data.EitherT
import cats.implicits.catsSyntaxEitherId
import de.innfactory.bootstrapplay2.application.controller.BaseController
import de.innfactory.play.smithy4play.ImplicitLogContext
import de.innfactory.bootstrapplay2.apidefinition.{
  LocationAPIController,
  LocationRequestBody,
  LocationResponse,
  LocationsResponse
}
import de.innfactory.bootstrapplay2.locations.application.mapper.LocationMapper
import de.innfactory.bootstrapplay2.locations.domain.interfaces.LocationService
import de.innfactory.bootstrapplay2.locations.domain.models.{Location, LocationCompanyId, LocationId}
import de.innfactory.play.controller.ResultStatus
import de.innfactory.smithy4play.{AutoRouting, ContextRoute}
import play.api.Application
import play.api.mvc.ControllerComponents

import javax.inject.Inject
import scala.concurrent.ExecutionContext

@AutoRouting
class LocationController @Inject() (locationService: LocationService)(implicit
    ec: ExecutionContext,
    app: Application,
    cc: ControllerComponents,
    mat: Materializer
) extends BaseController
    with ImplicitLogContext
    with LocationAPIController[ContextRoute]
    with LocationMapper {

  override def getAllLocationsByCompany(companyId: Long): ContextRoute[LocationsResponse] =
    Endpoint.withAuth
      .execute(locationService.getAllByCompany(LocationCompanyId(companyId))(_))
      .complete

  override def getAllLocations(): ContextRoute[LocationsResponse] =
    Endpoint.withAuth
      .execute(rc =>
        for {
          locationsStream <- locationService.getAllAsStream()(rc)
          locations <- EitherT(
            locationsStream
              .runFold(Seq.empty[Location])((list, location) => list :+ location)
              .map(_.asRight[ResultStatus])
          )
        } yield locations
      )
      .complete

  override def getLocationById(locationId: Long): ContextRoute[LocationResponse] =
    Endpoint.withAuth
      .execute(locationService.getById(LocationId(locationId))(_))
      .complete

  override def createLocation(body: LocationRequestBody): ContextRoute[LocationResponse] =
    Endpoint.withAuth
      .execute(locationService.createLocation(body)(_))
      .complete

  override def updateLocation(body: LocationRequestBody): ContextRoute[LocationResponse] =
    Endpoint.withAuth
      .execute(locationService.updateLocation(body)(_))
      .complete

  override def deleteLocation(locationId: Long): ContextRoute[Unit] =
    Endpoint.withAuth
      .execute(locationService.deleteLocation(LocationId(locationId))(_))
      .complete
}

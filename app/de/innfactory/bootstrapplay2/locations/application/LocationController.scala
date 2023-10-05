package de.innfactory.bootstrapplay2.locations.application

import akka.stream.Materializer
import cats.data.EitherT
import cats.implicits.catsSyntaxEitherId
import de.innfactory.bootstrapplay2.application.controller.BaseController
import de.innfactory.bootstrapplay2.api.{
  LocationAPIController,
  LocationRequestBody,
  LocationResponse,
  LocationsResponse
}
import de.innfactory.bootstrapplay2.companies.domain.models.CompanyId
import de.innfactory.bootstrapplay2.locations.application.mapper.LocationMapper
import de.innfactory.bootstrapplay2.locations.domain.interfaces.LocationService
import de.innfactory.bootstrapplay2.locations.domain.models.{Location, LocationId}
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
    with LocationAPIController[ContextRoute]
    with LocationMapper {

  override def getAllLocationsByCompany(
      companyId: de.innfactory.bootstrapplay2.api.CompanyId
  ): ContextRoute[LocationsResponse] =
    Endpoint.withAuth
      .execute(locationService.getAllByCompany(companyId)(_))
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

  override def getLocationById(
      locationId: de.innfactory.bootstrapplay2.api.LocationId
  ): ContextRoute[LocationResponse] =
    Endpoint.withAuth
      .execute(locationService.getById(locationId)(_))
      .complete

  override def createLocation(body: LocationRequestBody): ContextRoute[LocationResponse] =
    Endpoint.withAuth
      .execute(locationService.createLocation(body)(_))
      .complete

  override def updateLocation(body: LocationRequestBody): ContextRoute[LocationResponse] =
    Endpoint.withAuth
      .execute(locationService.updateLocation(body)(_))
      .complete

  override def deleteLocation(locationId: de.innfactory.bootstrapplay2.api.LocationId): ContextRoute[Unit] =
    Endpoint.withAuth
      .execute(locationService.deleteLocation(locationId)(_))
      .complete
}

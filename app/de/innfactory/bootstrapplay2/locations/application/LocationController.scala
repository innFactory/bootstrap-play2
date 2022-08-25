package de.innfactory.bootstrapplay2.locations.application

import akka.NotUsed
import akka.stream.scaladsl.Source
import cats.data.EitherT
import de.innfactory.bootstrapplay2.commons.application.actions.TracingUserAction
import de.innfactory.bootstrapplay2.commons.application.actions.models.RequestWithUser
import de.innfactory.play.smithy4play.ImplicitLogContext
import de.innfactory.bootstrapplay2.commons.results.Results
import de.innfactory.bootstrapplay2.definition
import de.innfactory.bootstrapplay2.definition.{LocationAPIController, LocationsResponse}
import de.innfactory.bootstrapplay2.locations.application.models.{LocationRequest, LocationResponse}
import de.innfactory.bootstrapplay2.locations.domain.interfaces.LocationService
import de.innfactory.bootstrapplay2.locations.domain.models.{Location, LocationCompanyId, LocationId}
import de.innfactory.play.controller.BaseController
import de.innfactory.smithy4play.ContextRoute
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import smithy4s.Document

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LocationController @Inject() (tracingUserAction: TracingUserAction, locationService: LocationService)(implicit
    ec: ExecutionContext,
    cc: ControllerComponents
) extends BaseController
    with ImplicitLogContext
    with LocationAPIController[ContextRoute] {

//  implicit def inMapperLocation(locationRequest: LocationRequest) = locationRequest.toLocation()
//
//  implicit val outMapperLocation: OutMapper[Location, LocationResponse] =
//    OutMapper(l => LocationResponse.fromLocation(l))
//
//  implicit val outMapperSeqLocation: OutMapper[Seq[Location], Seq[LocationResponse]] =
//    OutMapper(l => l.map(LocationResponse.fromLocation))
//
//  implicit val outMapperSourceLocation: OutMapper[Source[Location, NotUsed], Source[LocationResponse, NotUsed]] =
//    OutMapper(l => l.map(LocationResponse.fromLocation))
//
//  implicit private val outMapperBoolean: OutMapper[Boolean, Boolean] =
//    OutMapper[Boolean, Boolean](b => b)
//
//  def getAllByCompany(companyId: Long): Action[AnyContent] =
//    Endpoint
//      .in[RequestWithUser](tracingUserAction())
//      .logic((_, r) => locationService.getAllByCompany(LocationCompanyId(companyId))(requestContextWithUser(r)))
//      .mapOutTo[Seq[LocationResponse]]
//      .result(_.completeResult())
//
//  def getAllCompaniesAsSource: Action[AnyContent] =
//    Endpoint
//      .in[RequestWithUser](tracingUserAction())
//      .logic((_, r) => locationService.getAllAsStream()(requestContextWithUser(r)))
//      .mapOutTo[Source[LocationResponse, NotUsed]]
//      .result(_.completeSourceChunked())
//
//  def getById(id: Long): Action[AnyContent] =
//    Endpoint
//      .in[RequestWithUser](tracingUserAction())
//      .logic((_, r) => locationService.getById(LocationId(id))(requestContextWithUser(r)))
//      .mapOutTo[LocationResponse]
//      .result(_.completeResult())
//
//  def create(): Action[LocationRequest] =
//    Endpoint
//      .in[LocationRequest, RequestWithUser, Location](tracingUserAction())
//      .logic((l, r) => locationService.createLocation(l)(requestContextWithUser(r)))
//      .mapOutTo[LocationResponse]
//      .result(_.completeResult())
//
//  def update(): Action[LocationRequest] =
//    Endpoint
//      .in[LocationRequest, RequestWithUser, Location](tracingUserAction())
//      .logic((l, r) => locationService.updateLocation(l)(requestContextWithUser(r)))
//      .mapOutTo[LocationResponse]
//      .result(_.completeResult())
//
//  def delete(id: Long): Action[AnyContent] =
//    Endpoint
//      .in[RequestWithUser](tracingUserAction())
//      .logic((_, r) => locationService.deleteLocation(LocationId(id))(requestContextWithUser(r)))
//      .mapOutTo[Boolean]
//      .result(_.completeResultWithoutBody(204))

  override def getAllLocationsByCompany(companyId: String): ContextRoute[LocationsResponse] = ???

  override def getAllLocations(): ContextRoute[LocationsResponse] = ???

  override def getLocationById(locationId: String): ContextRoute[definition.LocationResponse] = ???

  override def createLocation(
      company: Long,
      id: Option[Long],
      name: Option[String],
      settings: Option[Document],
      addressLine1: Option[String],
      addressLine2: Option[String],
      zip: Option[String],
      city: Option[String],
      country: Option[String]
  ): ContextRoute[definition.LocationResponse] = ???

  override def updateLocation(
      company: Long,
      id: Option[Long],
      name: Option[String],
      settings: Option[Document],
      addressLine1: Option[String],
      addressLine2: Option[String],
      zip: Option[String],
      city: Option[String],
      country: Option[String]
  ): ContextRoute[definition.LocationResponse] = ???

  override def deleteLocation(locationId: String): ContextRoute[Unit] = ???
}

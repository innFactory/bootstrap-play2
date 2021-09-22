package de.innfactory.bootstrapplay2.locations.application

import akka.NotUsed
import akka.stream.scaladsl.Source
import cats.data.EitherT
import de.innfactory.bootstrapplay2.application.controller.BaseController
import de.innfactory.bootstrapplay2.commons.ReqConverterHelper.requestContextWithUser
import de.innfactory.bootstrapplay2.commons.application.actions.TracingUserAction
import de.innfactory.bootstrapplay2.commons.logging.ImplicitLogContext
import de.innfactory.bootstrapplay2.commons.results.Results
import de.innfactory.bootstrapplay2.locations.application.models.{ LocationRequest, LocationResponse }
import de.innfactory.bootstrapplay2.locations.domain.interfaces.LocationService
import de.innfactory.bootstrapplay2.locations.domain.models.{ Location, LocationCompanyId, LocationId }
import play.api.mvc.{ Action, AnyContent, ControllerComponents }

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

class LocationController @Inject() (tracingUserAction: TracingUserAction, locationService: LocationService)(implicit
  ec: ExecutionContext,
  cc: ControllerComponents
) extends BaseController
    with ImplicitLogContext {

  def getAllByCompany(companyId: Long): Action[AnyContent] = tracingUserAction().async { implicit request =>
    val result = for {
      getAll <- locationService.getAllByCompany(LocationCompanyId(companyId))(requestContextWithUser)
    } yield getAll.map(LocationResponse.fromLocation)
    result.completeResult()
  }

  def getAllCompaniesAsSource: Action[AnyContent] = tracingUserAction().async { implicit request =>
    val result: EitherT[Future, Results.ResultStatus, Source[LocationResponse, NotUsed]] = for {
      getAll <- locationService.getAllAsStream()(requestContextWithUser)
    } yield getAll.map(LocationResponse.fromLocation)
    result.completeSourceChunked()
  }

  def getById(id: Long): Action[AnyContent] = tracingUserAction().async { implicit request =>
    val companyId = LocationId(id)
    val result    = for {
      company <- locationService.getById(companyId)(requestContextWithUser)
    } yield LocationResponse.fromLocation(company)
    result.completeResult()
  }

  def create(): Action[LocationRequest] = tracingUserAction().async(validateJson[LocationRequest]) { implicit request =>
    val companyRequest = request.request.body
    val result         = for {
      company <- locationService.createLocation(companyRequest.toLocation())(requestContextWithUser)
    } yield LocationResponse.fromLocation(company)
    result.completeResult()
  }

  def update(): Action[LocationRequest] = tracingUserAction().async(validateJson[LocationRequest]) { implicit request =>
    val companyRequest = request.request.body
    val result         = for {
      company <- locationService.updateLocation(companyRequest.toLocation())(requestContextWithUser)
    } yield LocationResponse.fromLocation(company)
    result.completeResult()
  }

  def delete(id: Long): Action[AnyContent] = tracingUserAction().async { implicit request =>
    val companyId = LocationId(id)
    val result    = for {
      deleteResult <- locationService.deleteLocation(companyId)(requestContextWithUser)
    } yield deleteResult
    result.completeResultWithoutBody()
  }

}

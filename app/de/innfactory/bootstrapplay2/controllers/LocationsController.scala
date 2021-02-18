package de.innfactory.bootstrapplay2.controllers

import java.util.UUID
import javax.inject.{ Inject, Singleton }
import de.innfactory.bootstrapplay2.models.api.Location
import play.api.libs.json._
import play.api.mvc._
import cats.data.EitherT
import de.innfactory.bootstrapplay2.common.results.Results.ResultStatus
import de.innfactory.bootstrapplay2.common.validators.JsonValidator._
import cats.implicits._
import de.innfactory.bootstrapplay2.actions.TracingCompanyAction
import de.innfactory.bootstrapplay2.common.request.ReqConverterHelper.requestContextWithCompany
import de.innfactory.bootstrapplay2.repositories.LocationRepository

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class LocationsController @Inject() (
  cc: ControllerComponents,
  locationRepository: LocationRepository,
  tracingCompanyAction: TracingCompanyAction
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  def getSingle(
    id: Long
  ): Action[AnyContent] =
    tracingCompanyAction("get Single Location").async { implicit request =>
      locationRepository.lookup(id)(requestContextWithCompany).completeResult()
    }

  def getByDistance(
    distance: Long,
    lat: Double,
    lon: Double
  ): Action[AnyContent] =
    tracingCompanyAction("Get Locations By Distance").async { implicit request =>
      locationRepository.getByDistance(distance, lat, lon)(requestContextWithCompany).completeResult
    }

  def getByCompany(
    companyId: String
  ): Action[AnyContent] =
    tracingCompanyAction("Get Locations By Company").async { implicit request =>
      locationRepository.lookupByCompany(UUID.fromString(companyId))(requestContextWithCompany).completeResult
    }

  def patch: Action[AnyContent] =
    tracingCompanyAction("Patch Location").async { implicit request =>
      val json: JsValue                                   = request.body.asJson.get // Get the request body as json
      val stock                                           = json.as[Location]       // Json to Location Object
      val result: EitherT[Future, ResultStatus, Location] = for {
        _       <- EitherT(Future(json.validateFor[Location])) // Validate Json
        updated <- EitherT(
                     locationRepository.patch(stock)(requestContextWithCompany)
                   ) // call locationRepository to patch the object
      } yield updated
      result.value
        .completeResult() // get .value of EitherT and then .completeResult (implicit on Future[Either[ErrorStatus, ApiBaseModel]])
    }

  def post: Action[AnyContent] =
    tracingCompanyAction("Post Location").async { implicit request =>
      val json                                            = request.body.asJson.get
      val stock                                           = json.as[Location]
      val result: EitherT[Future, ResultStatus, Location] = for {
        _       <- EitherT(Future(json.validateFor[Location]))
        created <- EitherT(locationRepository.post(stock)(requestContextWithCompany))
      } yield created
      result.value.completeResult()
    }

  def delete(id: Long): Action[AnyContent] =
    tracingCompanyAction("Delete Location").async { implicit request =>
      locationRepository.delete(id)(requestContextWithCompany).completeResultWithoutBody(204)
    }

}

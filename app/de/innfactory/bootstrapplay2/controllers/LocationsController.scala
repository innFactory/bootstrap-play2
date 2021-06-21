package de.innfactory.bootstrapplay2.controllers

import javax.inject.{ Inject, Singleton }
import de.innfactory.bootstrapplay2.models.api.Location
import play.api.libs.json._
import play.api.mvc._
import cats.data.EitherT
import de.innfactory.bootstrapplay2.common.results.Results.ResultStatus
import de.innfactory.bootstrapplay2.common.validators.JsonValidator._
import cats.implicits._
import de.innfactory.bootstrapplay2.actions.TracingUserAction
import de.innfactory.bootstrapplay2.common.request.ReqConverterHelper.requestContextWithUser
import de.innfactory.bootstrapplay2.repositories.LocationRepository

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class LocationsController @Inject() (
  cc: ControllerComponents,
  locationRepository: LocationRepository,
  tracingUserAction: TracingUserAction
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  def getSingle(
    id: Long
  ): Action[AnyContent] =
    tracingUserAction("get Single Location").async { implicit request =>
      locationRepository.lookup(id)(requestContextWithUser).completeResult()
    }

  def getByDistance(
    distance: Long,
    lat: Double,
    lon: Double
  ): Action[AnyContent] =
    tracingUserAction("Get Locations By Distance").async { implicit request =>
      locationRepository.getByDistance(distance, lat, lon)(requestContextWithUser).completeResult
    }

  def getByCompany(
    companyId: Long
  ): Action[AnyContent] =
    tracingUserAction("Get Locations By Company").async { implicit request =>
      locationRepository.lookupByCompany(companyId)(requestContextWithUser).completeResult
    }

  def patch: Action[AnyContent] =
    tracingUserAction("Patch Location").async { implicit request =>
      val json: JsValue                                   = request.body.asJson.get // Get the request body as json
      val entity                                          = json.as[Location]       // Json to Location Object
      val result: EitherT[Future, ResultStatus, Location] = for {
        _       <- EitherT(Future(json.validateFor[Location])) // Validate Json
        updated <- EitherT(
                     locationRepository.patch(entity)(requestContextWithUser)
                   ) // call locationRepository to patch the object
      } yield updated
      result.value
        .completeResult() // get .value of EitherT and then .completeResult (implicit on Future[Either[ErrorStatus, ApiBaseModel]])
    }

  def post: Action[AnyContent] =
    tracingUserAction("Post Location").async { implicit request =>
      val json                                            = request.body.asJson.get
      val entity                                          = json.as[Location]
      val result: EitherT[Future, ResultStatus, Location] = for {
        _       <- EitherT(Future(json.validateFor[Location]))
        created <- EitherT(locationRepository.post(entity)(requestContextWithUser))
      } yield created
      result.value.completeResult()
    }

  def delete(id: Long): Action[AnyContent] =
    tracingUserAction("Delete Location").async { implicit request =>
      locationRepository.delete(id)(requestContextWithUser).completeResultWithoutBody(204)
    }

}

package controllers

import java.util.UUID
import javax.inject.{ Inject, Singleton }
import models.api.Location
import play.api.libs.json._
import play.api.mvc._
import actions._
import repositories.LocationRepository
import cats.data.EitherT
import common.results.Results.ErrorStatus
import common.validators.JsonValidator._
import models.api.Location.reads
import cats.implicits._
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class LocationController @Inject()(
  cc: ControllerComponents,
  locationRepository: LocationRepository,
  jwtValidationAction: JwtValidationAction,
  companyForUserExtractAction: CompanyForUserExtractAction
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  def getSingle(
    id: Long,
  ): Action[AnyContent] = jwtValidationAction.andThen(companyForUserExtractAction).async { implicit request =>
    locationRepository.lookup(id, request).completeResult()
  }

  def getByCompany(
    companyId: String
  ): Action[AnyContent] = jwtValidationAction.andThen(companyForUserExtractAction).async { implicit request =>
    locationRepository.lookupByCompany(UUID.fromString(companyId), request).completeResult
  }

  def patch: Action[AnyContent] =
    jwtValidationAction.andThen(companyForUserExtractAction).async { implicit request =>
      val json: JsValue = request.body.asJson.get // Get the request body as json
      val stock         = json.as[Location] // Json to Location Object
      val result: EitherT[Future, ErrorStatus, Location] = for {
        _       <- EitherT(Future(json.validateFor))                 // Validate Json
        updated <- EitherT(locationRepository.patch(stock, request)) // call locationRepository to patch the object
      } yield updated
      result.value
        .completeResult() // get .value of EitherT and then .completeResult (implicit on Future[Either[ErrorStatus, ApiBaseModel]])
    }

  def post: Action[AnyContent] = jwtValidationAction.andThen(companyForUserExtractAction).async { implicit request =>
    val json  = request.body.asJson.get
    val stock = json.as[Location]
    val result: EitherT[Future, ErrorStatus, Location] = for {
      _       <- EitherT(Future(json.validateFor))
      created <- EitherT(locationRepository.post(stock, request))
    } yield created
    result.value.completeResult()
  }

  def delete(id: Long): Action[AnyContent] =
    jwtValidationAction.andThen(companyForUserExtractAction).async { implicit request =>
      locationRepository.delete(id, request).completeResultWithoutBody(204)
    }

}

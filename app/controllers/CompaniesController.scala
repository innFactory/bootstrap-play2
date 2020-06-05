package controllers

import java.util.UUID

import actions.{ CompanyForUserExtractAction, JwtValidationAction }
import cats.data.EitherT
import cats.implicits._
import common.results.Results.ErrorStatus
import javax.inject.{ Inject, Singleton }
import models.api.Company
import play.api.mvc._
import repositories.CompaniesRepository
import common.validators.JsonValidator._
import common.utils.NilUtils._

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class CompaniesController @Inject()(
  cc: ControllerComponents,
  jwtValidationAction: JwtValidationAction,
  companyForUserExtractAction: CompanyForUserExtractAction,
  companiesRepository: CompaniesRepository
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  def getMe: Action[AnyContent] = jwtValidationAction.andThen(companyForUserExtractAction).async { implicit request =>
    val result = request.company match {
      case Some(company) => Right(company)
      case None          => Left(common.results.errors.Errors.NotFound())
    }
    Future(result).completeResult()
  }

  def getSingle(
    id: String,
  ): Action[AnyContent] = jwtValidationAction.andThen(companyForUserExtractAction).async { implicit request =>
    companiesRepository.lookup(UUID.fromString(id), request).completeResult()
  }

  def patch: Action[AnyContent] =
    jwtValidationAction.andThen(companyForUserExtractAction).async { implicit request =>
      val json  = request.body.asJson.get
      val stock = json.as[Company]
      val result: EitherT[Future, ErrorStatus, Company] = for {
        _       <- EitherT(Future(json.validateFor))
        created <- EitherT(companiesRepository.patch(stock, request))
      } yield created
      result.value.completeResult()
    }

  def post: Action[AnyContent] = jwtValidationAction.andThen(companyForUserExtractAction).async { implicit request =>
    val json  = request.body.asJson.get
    val stock = json.as[Company]
    val result: EitherT[Future, ErrorStatus, Company] = for {
      _       <- EitherT(Future(json.validateFor))
      created <- EitherT(companiesRepository.post(stock, request))
    } yield created
    result.value.completeResult()
  }

  def delete(id: String): Action[AnyContent] =
    jwtValidationAction.andThen(companyForUserExtractAction).async { implicit request =>
      companiesRepository.delete(UUID.fromString(id), request).completeResultWithoutBody(204)
    }

}

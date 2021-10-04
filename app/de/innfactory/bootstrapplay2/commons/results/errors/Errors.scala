package de.innfactory.bootstrapplay2.commons.results.errors

import de.innfactory.play.controller.ErrorResult

object Errors {

  case class DatabaseResult(
    message: String = "Entity or request malformed",
    additionalInfoToLog: Option[String] = None,
    additionalInfoErrorCode: Option[String] = None,
    statusCode: Int = 500
  ) extends ErrorResult()

  case class BadRequest(
    message: String = "Entity or request malformed",
    additionalInfoToLog: Option[String] = None,
    additionalInfoErrorCode: Option[String] = None,
    statusCode: Int = 400
  ) extends ErrorResult()

  case class NotFound(
    message: String = "Entity not found",
    additionalInfoToLog: Option[String] = None,
    additionalInfoErrorCode: Option[String] = None,
    statusCode: Int = 404
  ) extends ErrorResult()

  case class Forbidden(
    message: String = "Forbidden",
    additionalInfoToLog: Option[String] = None,
    additionalInfoErrorCode: Option[String] = None,
    statusCode: Int = 403
  ) extends ErrorResult()

  case class TokenValidationError(
    message: String = "TokenValidationError",
    additionalInfoToLog: Option[String] = None,
    additionalInfoErrorCode: Option[String] = None,
    statusCode: Int = 400
  ) extends ErrorResult()

  case class TokenExpiredError(
    message: String = "TokenExpiredError",
    additionalInfoToLog: Option[String] = None,
    additionalInfoErrorCode: Option[String] = None,
    statusCode: Int = 410
  ) extends ErrorResult()

}

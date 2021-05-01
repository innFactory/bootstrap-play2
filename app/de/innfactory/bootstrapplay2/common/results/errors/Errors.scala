package de.innfactory.bootstrapplay2.common.results.errors

import de.innfactory.bootstrapplay2.common.results.Results.NotLoggingResult

object Errors {

  case class DatabaseResult(
    message: String = "Entity or request malformed",
    additionalInfoToLog: Option[String] = None,
    additionalInfoErrorCode: Option[String] = None
  ) extends NotLoggingResult()

  case class BadRequest(
    message: String = "Entity or request malformed",
    additionalInfoToLog: Option[String] = None,
    additionalInfoErrorCode: Option[String] = None
  ) extends NotLoggingResult()

  case class NotFound(
    message: String = "Entity not found",
    additionalInfoToLog: Option[String] = None,
    additionalInfoErrorCode: Option[String] = None
  ) extends NotLoggingResult()

  case class Forbidden(
    message: String = "Forbidden",
    additionalInfoToLog: Option[String] = None,
    additionalInfoErrorCode: Option[String] = None
  ) extends NotLoggingResult()

}

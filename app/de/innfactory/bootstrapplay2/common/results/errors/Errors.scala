package de.innfactory.bootstrapplay2.common.results.errors

import de.innfactory.bootstrapplay2.common.results.Results.{ NotLoggingResult, SelfLoggingResult }

object Errors {
  case class DatabaseResult(message: String, errorClass: String, errorMethod: String, internalErrorMessage: String)
      extends SelfLoggingResult(message, 400, errorClass, errorMethod, internalErrorMessage)

  case class BadRequest() extends NotLoggingResult()

  case class NotFound() extends NotLoggingResult()

  case class Forbidden() extends NotLoggingResult()

}

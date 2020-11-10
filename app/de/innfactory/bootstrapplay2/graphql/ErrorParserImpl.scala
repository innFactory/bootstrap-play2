package de.innfactory.bootstrapplay2.graphql

import de.innfactory.bootstrapplay2.common.results.Results
import de.innfactory.bootstrapplay2.common.results.Results.ErrorStatus
import de.innfactory.bootstrapplay2.common.results.errors.Errors.{ BadRequest, Forbidden }
import de.innfactory.grapqhl.play.result.implicits.GraphQlResult.{ BadRequestError, ForbiddenError }
import de.innfactory.grapqhl.play.result.implicits.{ ErrorParser, GraphQlException }

class ErrorParserImpl extends ErrorParser[ErrorStatus] {
  override def internalErrorToUserFacingError(error: ErrorStatus): GraphQlException = error match {
    case _: BadRequest => BadRequestError("BadRequest")
    case _: Forbidden  => ForbiddenError("Forbidden")
    case _             => BadRequestError("BadRequest")
  }
}

package de.innfactory.bootstrapplay2.graphql

import de.innfactory.play.controller.ResultStatus
import de.innfactory.play.results.errors.Errors.{BadRequest, Forbidden}
import de.innfactory.grapqhl.play.result.implicits.GraphQlResult.{BadRequestError, ForbiddenError}
import de.innfactory.grapqhl.play.result.implicits.{ErrorParser, GraphQlException}

class ErrorParserImpl extends ErrorParser[ResultStatus] {
  override def internalErrorToUserFacingError(error: ResultStatus): GraphQlException = error match {
    case _: BadRequest => BadRequestError("BadRequest")
    case _: Forbidden  => ForbiddenError("Forbidden")
    case _             => BadRequestError("BadRequest")
  }
}

package graphql

import common.results.Results
import common.results.Results.ErrorStatus
import de.innfactory.grapqhl.play.result.implicits.GraphQlResult.BadRequestError
import de.innfactory.grapqhl.play.result.implicits.{ErrorParser, GraphQlException}

class ErrorParserImpl extends ErrorParser[ErrorStatus] {
  override def internalErrorToUserFacingError(error: ErrorStatus): GraphQlException = error match {
    case error => BadRequestError("BadRequest")
  }
}

package de.innfactory.bootstrapplay2.commons.results
import akka.stream.scaladsl.Source
import de.innfactory.bootstrapplay2.commons.results.errors.Errors._
import play.api.libs.json.{ Json, Writes }
import play.api.mvc.{ AnyContent, Request, Results => MvcResults }

import scala.concurrent.{ ExecutionContext, Future }

object Results {

  trait ResultStatus

  abstract class NotLoggingResult() extends ResultStatus {
    def message: String
    def additionalInfoToLog: Option[String]
    def additionalInfoErrorCode: Option[String]
    def statusCode: Int
  }

  type Result[T] = Either[ResultStatus, T]

}

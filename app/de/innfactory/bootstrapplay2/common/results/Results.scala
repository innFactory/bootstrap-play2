package de.innfactory.bootstrapplay2.common.results
import akka.stream.scaladsl.Source
import de.innfactory.bootstrapplay2.common.results.errors.Errors._
import play.api.libs.json.{ Json, Writes }
import play.api.mvc.{ AnyContent, Request, Results => MvcResults }

import scala.concurrent.{ ExecutionContext, Future }

object Results {

  trait ResultStatus

  abstract class NotLoggingResult() extends ResultStatus {
    def message: String
    def additionalInfoToLog: Option[String]
    def additionalInfoErrorCode: Option[String]
  }

  type Result[T] = Either[ResultStatus, T]

  implicit class RichError(value: ResultStatus)(implicit ec: ExecutionContext) {
    def mapToResult: play.api.mvc.Result =
      value match {
        case e: DatabaseResult => MvcResults.Status(500)(ErrorResponse.fromMessage(e.message))
        case e: Forbidden      => MvcResults.Status(403)(ErrorResponse.fromMessage(e.message))
        case e: BadRequest     => MvcResults.Status(400)(ErrorResponse.fromMessage(e.message))
        case e: NotFound       => MvcResults.Status(404)(ErrorResponse.fromMessage(e.message))
        case _                 => MvcResults.Status(400)("")
      }
  }

  implicit class RichResult[T](value: Future[Either[ResultStatus, T]])(implicit ec: ExecutionContext) {
    def completeResult(statusCode: Int = 200)(implicit writes: Writes[T]): Future[play.api.mvc.Result] =
      value.map {
        case Left(error: ResultStatus) => error.mapToResult
        case Right(value: T)           => MvcResults.Status(statusCode)(Json.toJson(value))
      }

    def completeResultWithoutBody(statusCode: Int = 200): Future[play.api.mvc.Result] =
      value.map {
        case Left(error: ResultStatus) => error.mapToResult
        case Right(_: T)               => MvcResults.Status(statusCode)("")
      }
  }

  implicit class RichSeqResult[T](value: Future[Either[ResultStatus, Seq[T]]])(implicit ec: ExecutionContext) {
    def completeResult(implicit writes: Writes[T]): Future[play.api.mvc.Result] =
      value.map {
        case Left(error: ResultStatus) => error.mapToResult
        case Right(value: Seq[T])      => MvcResults.Status(200)(Json.toJson(value))
      }
  }

  implicit class RichSourceResult[T](value: Future[Either[ResultStatus, Source[T, _]]])(implicit
    ec: ExecutionContext,
    request: Request[AnyContent]
  ) {
    def completeSourceChunked()(implicit writes: Writes[T]): Future[play.api.mvc.Result] =
      value.map {
        case Left(error: ResultStatus)  => error.mapToResult
        case Right(value: Source[T, _]) =>
          MvcResults
            .Status(200)
            .chunked(
              value.map(Json.toJson(_).toString).intersperse("[", ",", "]"),
              Some("application/json")
            )
        case _                          => MvcResults.Status(500)("")
      }
  }

}

package de.innfactory.bootstrapplay2.application.controller

import akka.stream.scaladsl.Source
import de.innfactory.bootstrapplay2.commons.results.ErrorResponse
import de.innfactory.bootstrapplay2.commons.results.Results.{ NotLoggingResult, ResultStatus }
import play.api.libs.json.{ JsError, Json, Reads, Writes }
import play.api.mvc.{ AbstractController, AnyContent, BodyParser, ControllerComponents, Request, Results => MvcResults }
import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

class BaseController(implicit cc: ControllerComponents, ec: ExecutionContext) extends AbstractController(cc) {

  protected def validateJson[A: Reads]: BodyParser[A] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  private implicit class RichError(value: ResultStatus)(implicit ec: ExecutionContext) {
    def mapToResult: play.api.mvc.Result =
      value match {
        case e: NotLoggingResult => MvcResults.Status(e.statusCode)(ErrorResponse.fromMessage(e.message))
        case _                   => MvcResults.Status(400)("")
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
    def completeResult()(implicit writes: Writes[T]): Future[play.api.mvc.Result] =
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
        case _                          => MvcResults.Status(500)("could not resolve source")
      }
  }

}

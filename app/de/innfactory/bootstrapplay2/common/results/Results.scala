package de.innfactory.bootstrapplay2.common.results
import de.innfactory.bootstrapplay2.common.results.errors.Errors._
import de.innfactory.bootstrapplay2.models.api.ApiBaseModel
import play.api.Logger
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.{ Results => MvcResults }

import scala.concurrent.{ ExecutionContext, Future }

object Results {

  private val logger = Logger("application")

  trait ResultStatus

  /**
   * Extend from this error class to have the error logging itself
   * @param message
   * @param statusCode
   * @param errorClass
   * @param errorMethod
   * @param internalErrorMessage
   */
  abstract class SelfLoggingResult(
    message: String,
    statusCode: Int,
    errorClass: String,
    errorMethod: String,
    internalErrorMessage: String
  ) extends ResultStatus {
    var currentStackTrace = new Throwable()
    logger.error(
      s"DatabaseError | message=$message statusCode=$statusCode | Error in class $errorClass in method $errorMethod $internalErrorMessage!",
      currentStackTrace
    )
  }

  abstract class NotLoggingResult() extends ResultStatus

  type Result[T] = Either[ResultStatus, T]

  implicit class RichError(value: ResultStatus)(implicit ec: ExecutionContext) {
    def mapToResult: play.api.mvc.Result =
      value match {
        case _: DatabaseResult => MvcResults.Status(500)("")
        case _: Forbidden      => MvcResults.Status(403)("")
        case _: BadRequest     => MvcResults.Status(400)("")
        case _: NotFound       => MvcResults.Status(404)("")
        case _                 => MvcResults.Status(400)("")
      }
  }

  implicit class SeqApiBaseModel(value: Seq[ApiBaseModel]) {
    def toJson: JsValue = Json.toJson(value.map(_.toJson))
  }

  implicit class RichResult(value: Future[Either[ResultStatus, ApiBaseModel]])(implicit ec: ExecutionContext) {
    def completeResult(statusCode: Int = 200): Future[play.api.mvc.Result] =
      value.map {
        case Left(error: ResultStatus)  => error.mapToResult
        case Right(value: ApiBaseModel) => MvcResults.Status(statusCode)(value.toJson)
      }

    def completeResultWithoutBody(statusCode: Int = 200): Future[play.api.mvc.Result] =
      value.map {
        case Left(error: ResultStatus)  => error.mapToResult
        case Right(value: ApiBaseModel) => MvcResults.Status(statusCode)("")
      }
  }

  implicit class RichSeqResult(value: Future[Either[ResultStatus, Seq[ApiBaseModel]]])(implicit ec: ExecutionContext) {
    def completeResult: Future[play.api.mvc.Result] =
      value.map {
        case Left(error: ResultStatus)       => error.mapToResult
        case Right(value: Seq[ApiBaseModel]) => MvcResults.Status(200)(value.toJson)
      }
  }

}

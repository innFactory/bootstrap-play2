package common.results
import common.results.errors.Errors._
import models.api.ApiBaseModel
import play.api.Logger
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.{ Results => MvcResults }

import scala.concurrent.{ ExecutionContext, Future }

object Results {

  private val logger = Logger("application")

  trait ErrorStatus

  /**
   * Extend from this error class to have the error logging itself
   * @param message
   * @param statusCode
   * @param errorClass
   * @param errorMethod
   * @param internalErrorMessage
   */
  abstract class SelfLoggingError(message: String,
                                  statusCode: Int,
                                  errorClass: String,
                                  errorMethod: String,
                                  internalErrorMessage: String)
      extends ErrorStatus {
    var currentStackTrace = new Throwable()
    logger.error(
      s"DatabaseError | message=$message statusCode=$statusCode | Error in class $errorClass in method $errorMethod $internalErrorMessage!",
      currentStackTrace
    )
  }

  abstract class NotLoggingError() extends ErrorStatus

  type Result[T] = Either[ErrorStatus, T]

  implicit class RichError(value: ErrorStatus)(implicit ec: ExecutionContext) {
    def mapToResult: play.api.mvc.Result = value match {
      case _: DatabaseError => MvcResults.Status(500)("")
      case _: Forbidden     => MvcResults.Status(403)("")
      case _: BadRequest    => MvcResults.Status(400)("")
      case _: NotFound      => MvcResults.Status(404)("")
      case _                => MvcResults.Status(400)("")
    }
  }

  implicit class SeqApiBaseModel(value: Seq[ApiBaseModel]) {
    def toJson: JsValue = Json.toJson(value.map(_.toJson))
  }

  implicit class RichResult(value: Future[Either[ErrorStatus, ApiBaseModel]])(implicit ec: ExecutionContext) {
    def completeResult(statusCode: Int = 200): Future[play.api.mvc.Result] = value.map {
      case Left(error: ErrorStatus)   => error.mapToResult
      case Right(value: ApiBaseModel) => MvcResults.Status(statusCode)(value.toJson)
    }

    def completeResultWithoutBody(statusCode: Int = 200): Future[play.api.mvc.Result] = value.map {
      case Left(error: ErrorStatus)   => error.mapToResult
      case Right(value: ApiBaseModel) => MvcResults.Status(statusCode)("")
    }
  }

  implicit class RichSeqResult(value: Future[Either[ErrorStatus, Seq[ApiBaseModel]]])(implicit ec: ExecutionContext) {
    def completeResult: Future[play.api.mvc.Result] = value.map {
      case Left(error: ErrorStatus)        => error.mapToResult
      case Right(value: Seq[ApiBaseModel]) => MvcResults.Status(200)(value.toJson)
    }
  }

}

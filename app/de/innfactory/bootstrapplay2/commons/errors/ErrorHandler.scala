package de.innfactory.bootstrapplay2.commons.errors

import de.innfactory.bootstrapplay2.commons.results.ErrorResponseWithAdditionalBody
import de.innfactory.play.controller.ErrorResponse
import play.api.http.HttpErrorHandler
import play.api.mvc._
import play.api.mvc.Results._

import scala.concurrent._
import javax.inject.Singleton
import play.api.Logger

@Singleton
class ErrorHandler extends HttpErrorHandler {

  /**
   * Custom error handler for custom client error handling
   * @param request
   * @param statusCode
   * @param message
   * @return
   */
  def onClientError(request: RequestHeader, statusCode: Int, message: String) =
    Future.successful(
      Status(statusCode)("A client error occurred")
    )

  case class JsonError(jsonPath: String, errorDetails: String)

  import play.api.libs.json.Json
  object JsonError {
    implicit val format = Json.format[JsonError]
  }

  /**
   *  Custom error handler for custom server error handling
   * @param request
   * @param exception
   * @return
   */
  def onServerError(request: RequestHeader, exception: Throwable) = {
    val log = Logger("play")
    Future.successful(exception match {
      case e: play.api.libs.json.JsResultException =>
        BadRequest(
          ErrorResponseWithAdditionalBody(
            "Invalid Json",
            Json.toJson(
              e.errors.map(error =>
                JsonError(
                  error._1.path.mkString(", "),
                  error._2.flatMap(_.messages).mkString(", ")
                )
              )
            )
          ).toJson
        )
      case pg: org.postgresql.util.PSQLException   =>
        log.error(request.toString(), exception)
        BadRequest(ErrorResponse("PSQL Exception, maybe duplicate key or foreign key constraint").toJson)
      case _                                       =>
        println(exception)
        println(request)
        log.error(request.toString(), exception)
        InternalServerError(ErrorResponse("unknown internal server error").toJson)
    })

  }
}

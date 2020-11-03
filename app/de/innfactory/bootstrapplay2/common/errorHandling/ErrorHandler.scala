package de.innfactory.bootstrapplay2.common.errorHandling

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

  /**
   *  Custom error handler for custom server error handling
   * @param request
   * @param exception
   * @return
   */
  def onServerError(request: RequestHeader, exception: Throwable) = {
    val log = Logger("play")
    log.error(request.toString(), exception)
    Future.successful(
      InternalServerError("A server error occurred")
    )
  }
}

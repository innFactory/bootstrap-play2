package de.innfactory.bootstrapplay2.filters.logging

import javax.inject.Inject
import akka.stream.Materializer
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.Logger
import play.api.mvc._
import play.api._

class AccessLoggingFilter @Inject() (config: Config, implicit val mat: Materializer) extends Filter {
  val accessLogger = Logger("AccessFilterLog")

  /**
   * status list from application.conf
   */
  private val configAccessStatus =
    config.getIntList("logging.access.statusList ")

  /**
   * Logs requests if result header status is inclueded
   * in logging.access.statusList as defined in application.conf
   * @param next
   * @param request
   * @return
   */
  def apply(next: RequestHeader => Future[Result])(request: RequestHeader): Future[Result] = {
    val resultFuture = next(request)
    resultFuture.foreach { result =>
      if (shouldBeLogged(result)) {
        val msg =
          s"RequestID: status=${result.header.status} method=${request.method} uri=${request.uri} remote-address=${request.remoteAddress}" +
            s" authorization-header=${request.headers.get("Authorization")}"
        accessLogger.warn(msg)
      }
    }
    resultFuture
  }

  /**
   * check if request/result should be logged
   * @param result
   * @return
   */
  def shouldBeLogged(result: Result): Boolean =
    configAccessStatus.contains(result.header.status)

}

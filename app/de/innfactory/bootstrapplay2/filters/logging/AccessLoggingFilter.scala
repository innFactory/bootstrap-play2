package de.innfactory.bootstrapplay2.filters.logging

import javax.inject.Inject
import akka.stream.Materializer
import com.typesafe.config.Config
import de.innfactory.smithy4play
import de.innfactory.smithy4play.{ContextRouteError, EndpointResult, RouteResult, RoutingContext}
import de.innfactory.smithy4play.middleware.MiddlewareBase

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.Logger
import play.api.mvc._
import play.api._

class AccessLoggingFilter @Inject() (config: Config, implicit val mat: Materializer) extends MiddlewareBase {
  val accessLogger = Logger("AccessFilterLog")

  /**
   * status list from application.conf
   */
  private val configAccessStatus =
    config.getIntList("logging.access.statusList")

  /**
   * Logs requests if result header status is inclueded in logging.access.statusList as defined in application.conf
   */
  override protected def logic(
      r: RoutingContext,
      next: RoutingContext => RouteResult[EndpointResult]
  ): RouteResult[EndpointResult] = {
    val request = r.requestHeader
    val result = next(r)
    result.leftMap { e =>
      if (shouldBeLogged(e)) {
        val msg =
          s"RequestID: status=${e.statusCode} method=${request.method} uri=${request.uri} remote-address=${request.remoteAddress}" +
            s" authorization-header=${request.headers.get("Authorization")}"
        accessLogger.warn(msg)
      }
      e
    }
  }

  /**
   * check if request/result should be logged
   */
  def shouldBeLogged(e: ContextRouteError): Boolean =
    configAccessStatus.contains(e.statusCode)

}

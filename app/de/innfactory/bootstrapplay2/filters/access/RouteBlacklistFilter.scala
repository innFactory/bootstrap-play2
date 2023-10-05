package de.innfactory.bootstrapplay2.filters.access

import javax.inject.Inject
import akka.stream.Materializer
import com.typesafe.config.Config
import de.innfactory.smithy4play
import de.innfactory.smithy4play.{RouteResult, RoutingContext}
import de.innfactory.smithy4play.middleware.MiddlewareBase
import play.api.mvc.Results.NotFound

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.Logger
import play.api.Mode.Prod
import play.api.mvc._
import play.api._

class RouteBlacklistFilter @Inject() (config: Config, implicit val mat: Materializer, environment: Environment)
    extends MiddlewareBase {

  case class BlackListEntry(route: String, environment: Mode, method: String)

  private val accessLogger = Logger("AccessFilterLog")

  private val blacklistedRoutes = Seq[BlackListEntry]()

  /**
   * Check if route is contained in blacklistedRoutes and block request if true
   */
  override protected def skipMiddleware(r: RoutingContext): Boolean = {
    val path = r.requestHeader.path
    val method = r.requestHeader.method
    for (route <- blacklistedRoutes)
      if (environment.mode == route.environment && path.startsWith(route.route) && route.method == method)
        accessLogger.logger.warn(s"Illegal access to $path with $method in production")
    return true
    false
  }

  override protected def logic(
      r: RoutingContext,
      next: RoutingContext => RouteResult[smithy4play.EndpointResult]
  ): RouteResult[smithy4play.EndpointResult] =
    next.apply(r)
}

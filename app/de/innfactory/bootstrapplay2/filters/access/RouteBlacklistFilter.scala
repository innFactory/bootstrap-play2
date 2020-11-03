package de.innfactory.bootstrapplay2.filters.access

import javax.inject.Inject
import akka.stream.Materializer
import com.typesafe.config.Config
import play.api.mvc.Results.NotFound

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.Logger
import play.api.Mode.Prod
import play.api.mvc._
import play.api._

class RouteBlacklistFilter @Inject() (config: Config, implicit val mat: Materializer, environment: Environment)
    extends Filter {

  case class BlackListEntry(route: String, environment: Mode, method: String)

  private val accessLogger = Logger("AccessFilterLog")

  private val blacklistedRoutes = Seq[BlackListEntry](
    BlackListEntry("/v1/assets/swagger.json", Prod, "GET")
  )

  /**
   * Check if route is contained in blacklistedRoutes
   * and block request if true
   * @param nextFilter
   * @param request
   * @return
   */
  def apply(nextFilter: RequestHeader => Future[Result])(request: RequestHeader): Future[Result] =
    if (shouldBeBlocked(request.path, request.method)) {
      accessLogger.logger.warn("Illegal access to swagger.json in production")
      Future(NotFound(""))
    } else
      nextFilter.apply(request)

  def shouldBeBlocked(path: String, method: String): Boolean = {
    for (route <- blacklistedRoutes)
      if (environment.mode == route.environment && path.startsWith(route.route) && route.method == method)
        return true
    false
  }

}

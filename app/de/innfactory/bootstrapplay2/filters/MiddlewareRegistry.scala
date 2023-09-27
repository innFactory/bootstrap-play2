package de.innfactory.bootstrapplay2.filters

import de.innfactory.bootstrapplay2.filters.access.RouteBlacklistFilter
import de.innfactory.bootstrapplay2.filters.logging.AccessLoggingFilter
import de.innfactory.bootstrapplay2.filters.tracing.TracingFilter
import de.innfactory.smithy4play.middleware.{MiddlewareBase, MiddlewareRegistryBase, ValidateAuthMiddleware}

import javax.inject.Inject

class MiddlewareRegistry @Inject() (
    routeBlacklistFilter: RouteBlacklistFilter,
    accessLoggingFilter: AccessLoggingFilter,
    tracingFilter: TracingFilter,
    validateAuthMiddleware: ValidateAuthMiddleware
) extends MiddlewareRegistryBase {
  override val middlewares: Seq[MiddlewareBase] =
    Seq(tracingFilter, routeBlacklistFilter, accessLoggingFilter, validateAuthMiddleware)
}

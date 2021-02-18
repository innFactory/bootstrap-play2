package de.innfactory.bootstrapplay2.common.request.logger

import de.innfactory.bootstrapplay2.common.logging.LogContext
import io.opencensus.trace.Span
import org.slf4j.{ Marker, MarkerFactory }
import play.api.Logger
import play.api.libs.json.Json

class TraceLogger(span: Span) {
  private val logger: org.slf4j.Logger = Logger.apply("request-context").logger

  private def getMarker(span: Span)(implicit logContext: LogContext): Marker =
    MarkerFactory.getMarker(spanToMarker(span))

  private def spanToMarker(span: Span)(implicit logContext: LogContext): String =
    Json.prettyPrint(Json.toJson(logContext.toLogbackContext(span.getContext.getTraceId.toLowerBase16)))

  def warn(message: String)(implicit logContext: LogContext): Unit =
    logger.warn(getMarker(span), message)

  def error(message: String)(implicit logContext: LogContext): Unit =
    logger.error(getMarker(span), message)

  def info(message: String)(implicit logContext: LogContext): Unit =
    logger.info(getMarker(span), message)

  def debug(message: String)(implicit logContext: LogContext): Unit =
    logger.info(getMarker(span), message)
}

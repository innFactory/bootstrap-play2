package de.innfactory.bootstrapplay2.commons.logging

import io.opentelemetry.api.trace.Span
import org.slf4j.{Marker, MarkerFactory}
import play.api.Logger

object LoggingEnhancer {

  private def spanToMarker(span: Span): String =
    "tracer=" + span.getSpanContext.getTraceId

  private def getMarker(span: Span): Marker =
    MarkerFactory.getMarker(spanToMarker(span))

  implicit class LoggingEnhancer(logger: Logger) {
    def tracedWarn(message: String)(implicit span: Span) =
      logger.logger.warn(getMarker(span), message)

    def tracedError(message: String)(implicit span: Span) =
      logger.logger.error(getMarker(span), message)

    def tracedInfo(message: String)(implicit span: Span) =
      logger.logger.info(getMarker(span), message)

    def tracedDebug(message: String)(implicit span: Span) =
      logger.logger.info(getMarker(span), message)
  }

}

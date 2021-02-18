package de.innfactory.bootstrapplay2.common.logging

import de.innfactory.play.logging.logback.LogbackContext

trait ImplicitLogContext {
  implicit val logContext = LogContext(this.getClass.getName)
}

case class LogContext(className: String) {
  def toLogbackContext(traceId: String): LogbackContext =
    LogbackContext(className = Some(className), trace = Some(traceId))
}

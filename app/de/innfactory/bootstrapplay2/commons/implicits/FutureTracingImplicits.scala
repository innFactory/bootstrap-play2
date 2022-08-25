package de.innfactory.bootstrapplay2.commons.implicits

import de.innfactory.play.smithy4play.TraceContext
import io.opencensus.scala.Tracing.traceWithParent

import scala.concurrent.{ExecutionContext, Future}

object FutureTracingImplicits {

  implicit class EnhancedFuture[T](future: Future[T]) {
    def trace(
        string: String
    )(implicit tc: TraceContext, ec: ExecutionContext): Future[T] =
      tc.span
        .map(span =>
          traceWithParent(string, span) { _ =>
            future
          }
        )
        .getOrElse(future)
  }
}

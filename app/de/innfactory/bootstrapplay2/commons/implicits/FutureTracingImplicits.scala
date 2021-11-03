package de.innfactory.bootstrapplay2.commons.implicits

import cats.data.EitherT
import cats.implicits.catsSyntaxEitherId
import de.innfactory.play.controller.ResultStatus
import de.innfactory.bootstrapplay2.commons.TraceContext
import io.opencensus.scala.Tracing.traceWithParent
import io.opencensus.trace.Span

import scala.concurrent.{ ExecutionContext, Future }

object FutureTracingImplicits {

  implicit class EnhancedFuture[T](future: Future[T]) {
    def trace(
      string: String
    )(implicit tc: TraceContext, ec: ExecutionContext): Future[T] =
      traceWithParent(string, tc.span) { _ =>
        future
      }
  }

  def TracedT[A](
    string: String
  )(implicit tc: TraceContext, ec: ExecutionContext): EitherT[Future, ResultStatus, Span] =
    EitherT(traceWithParent(string, tc.span) { span =>
      Future(span.asRight[ResultStatus])
    })

}
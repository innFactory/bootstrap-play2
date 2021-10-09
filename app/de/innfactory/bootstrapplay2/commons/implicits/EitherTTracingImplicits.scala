package de.innfactory.bootstrapplay2.commons.implicits

import cats.data.EitherT
import cats.implicits.catsSyntaxEitherId
import de.innfactory.play.controller.ResultStatus
import de.innfactory.bootstrapplay2.commons.{ RequestContext, TraceContext }
import io.opencensus.scala.Tracing.traceWithParent
import io.opencensus.trace.Span

import scala.concurrent.{ ExecutionContext, Future }

object EitherTTracingImplicits {

  implicit class EnhancedTracingEitherT[T](eitherT: EitherT[Future, ResultStatus, T]) {
    def trace[A](
      string: String
    )(implicit rc: TraceContext, ec: ExecutionContext): EitherT[Future, ResultStatus, T] =
      EitherT(traceWithParent(string, rc.span) { span =>
        eitherT.value
      })
  }

  def TracedT[A](
    string: String
  )(implicit rc: TraceContext, ec: ExecutionContext): EitherT[Future, ResultStatus, Span] =
    EitherT(traceWithParent(string, rc.span) { span =>
      Future(span.asRight[ResultStatus])
    })

}

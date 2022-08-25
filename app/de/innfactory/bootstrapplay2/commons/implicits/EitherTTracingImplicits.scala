package de.innfactory.bootstrapplay2.commons.implicits

import cats.data.EitherT
import de.innfactory.play.controller.ResultStatus
import de.innfactory.play.smithy4play.TraceContext
import io.opencensus.scala.Tracing.traceWithParent

import scala.concurrent.{ExecutionContext, Future}

object EitherTTracingImplicits {

  implicit class EnhancedTracingEitherT[T](eitherT: EitherT[Future, ResultStatus, T]) {
    def trace[A](
        string: String
    )(implicit rc: TraceContext, ec: ExecutionContext): EitherT[Future, ResultStatus, T] =
      rc.span
        .map(span =>
          EitherT(traceWithParent(string, span) { _ =>
            eitherT.value
          })
        )
        .getOrElse(eitherT)
  }
}

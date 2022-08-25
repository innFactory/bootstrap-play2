package de.innfactory.bootstrapplay2.commons.implicits

import de.innfactory.bootstrapplay2.commons.RequestContext
import de.innfactory.play.smithy4play.HttpHeaders
import io.opencensus.scala.Tracing.{startSpanWithRemoteParent, traceWithParent}
import io.opencensus.trace.{SpanContext, SpanId, TraceId, TraceOptions, Tracestate}
import play.api.mvc.{AnyContent, Request}

import scala.concurrent.{ExecutionContext, Future}

object RequestToRequestContextImplicit {

  implicit class EnhancedRequest(request: Request[AnyContent]) {
    def toRequestContextAndExecute[T](spanString: String, f: RequestContext => Future[T])(implicit
        ec: ExecutionContext
    ): Future[T] = {
      val headerTracingId = request.headers.get("X-Tracing-ID").get
      val spanId = request.headers.get("X-Internal-SpanId").get
      val traceId = request.headers.get("X-Internal-TraceId").get
      val traceOptions = request.headers.get("X-Internal-TraceOption").get

      val span = startSpanWithRemoteParent(
        headerTracingId,
        SpanContext.create(
          TraceId.fromLowerBase16(traceId),
          SpanId.fromLowerBase16(spanId),
          TraceOptions.fromLowerBase16(traceOptions, 0),
          Tracestate.builder().build()
        )
      )

      traceWithParent(spanString, span) { spanChild =>
        val rc = new RequestContext(HttpHeaders(request.headers.toMap), Some(spanChild))
        val result = f(rc)
        result.map { r =>
          spanChild.end()
          span.end()
          r
        }
      }
    }
  }

}

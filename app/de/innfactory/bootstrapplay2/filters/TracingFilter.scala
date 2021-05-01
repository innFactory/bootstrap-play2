package de.innfactory.bootstrapplay2.filters

import akka.stream.Materializer
import com.typesafe.config.Config
import de.innfactory.bootstrapplay2.common.tracing.Common.GoogleAttributes._
import de.innfactory.bootstrapplay2.common.tracing.Common.{
  XTRACINGID,
  X_INTERNAL_SPANID,
  X_INTERNAL_TRACEID,
  X_INTERNAL_TRACEOPTIONS
}
import org.joda.time.DateTime
import play.api.mvc._
import io.opencensus.scala.Tracing._
import io.opencensus.stats.Measure.MeasureDouble
import io.opencensus.stats.Stats
import io.opencensus.trace.samplers.Samplers
import io.opencensus.trace.{ AttributeValue, Sampler, SpanBuilder, Tracing }

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TracingFilter @Inject() (config: Config, implicit val mat: Materializer) extends Filter {

  private val statsRecorder = Stats.getStatsRecorder
  private val LATENCY_MS    = MeasureDouble.create("task_latency", "The task latency in milliseconds", "ms")

  def apply(next: RequestHeader => Future[Result])(request: RequestHeader): Future[Result] = {
    // Start Trace Span Root
    val sampler =
      if (request.headers.get(XTRACINGID).isDefined)
        Samplers.alwaysSample()
      else
        Samplers.probabilitySampler(1.00)

    val span = Tracing.getTracer.spanBuilder(request.path).setSampler(sampler).startSpan()

    var xTracingId = (XTRACINGID, span.getContext.getTraceId.toLowerBase16)

    if (request.headers.get(XTRACINGID).isDefined)
      xTracingId = (XTRACINGID, request.headers.get(XTRACINGID).get)

    // Add Annotations and Attributes to newly created Root Trace
    span.addAnnotation("TracingFilter")
    span.putAttribute("Position", AttributeValue.stringAttributeValue("TracingFilter"))
    span.putAttribute(HTTP_URL, AttributeValue.stringAttributeValue(request.host + request.uri))
    span.putAttribute(HTTP_HOST, AttributeValue.stringAttributeValue(request.host))
    span.putAttribute(HTTP_METHOD, AttributeValue.stringAttributeValue(request.method))

    // Add new Span to internal Request
    val newRequestHeaders = request.headers
      .add(xTracingId)
      .add((X_INTERNAL_SPANID, span.getContext.getSpanId.toLowerBase16))
      .add((X_INTERNAL_TRACEID, span.getContext.getTraceId.toLowerBase16))
      .add((X_INTERNAL_TRACEOPTIONS, span.getContext.getTraceOptions.toLowerBase16))

    // Call Next Filter with new Headers
    val result = next(request.withHeaders(newRequestHeaders))

    // Check Start Time
    val start = DateTime.now

    // Process Result
    result.map { res =>
      // Add more Attributes to trace
      span.putAttribute(HTTP_STATUS_CODE, AttributeValue.longAttributeValue(res.header.status))
      span.putAttribute(STATUS, AttributeValue.longAttributeValue(res.header.status))
      span.putAttribute(HTTP_RESPONSE_SIZE, AttributeValue.longAttributeValue(res.body.contentLength.getOrElse(0)))

      // Finish Root Span
      span.end()

      // Check end Time
      val end = DateTime.now

      // Add Metric with Span Processing Time
      statsRecorder.newMeasureMap.put(LATENCY_MS, end.getMillis - start.getMillis).record()

      // Add xTracingId to Result Header
      res.withHeaders(xTracingId)
    }

  }

}

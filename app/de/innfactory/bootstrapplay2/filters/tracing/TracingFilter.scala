package de.innfactory.bootstrapplay2.filters.tracing

import akka.stream.Materializer
import com.google.cloud.opentelemetry.shadow.semconv.trace.attributes.SemanticAttributes
import com.typesafe.config.Config
import de.innfactory.play.smithy4play.ImplicitLogContext
import de.innfactory.play.tracing.GoogleTracingIdentifier.GoogleAttributes.{
  HTTP_RESPONSE_SIZE,
  HTTP_STATUS_CODE,
  STATUS
}
import de.innfactory.play.tracing.GoogleTracingIdentifier.XTRACINGID
import de.innfactory.play.tracing.{OpentelemetryProvider, TraceLogger}
import de.innfactory.smithy4play
import de.innfactory.smithy4play.middleware.MiddlewareBase
import de.innfactory.smithy4play.{EndpointResult, RouteResult, RoutingContext}
import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.context.Context
import org.joda.time.DateTime
import play.api.Logger
import play.api.mvc._

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global

class TracingFilter @Inject() (config: Config, implicit val mat: Materializer)
    extends MiddlewareBase
    with ImplicitLogContext {
  private val healthEndpoints: Map[String, Seq[String]] = Map("GET" -> Seq("/", "/liveness", "/readiness"))
  private val healthLogger = Logger("health").logger
  private val configAccessStatus = config.getIntList("logging.access.statusList")

  override protected def logic(
      r: RoutingContext,
      next: RoutingContext => RouteResult[smithy4play.EndpointResult]
  ): RouteResult[smithy4play.EndpointResult] = if (
    !healthEndpoints.getOrElse(r.requestHeader.method, Seq.empty).contains(r.requestHeader.path)
  )
    handleLoggingAndTracing(next, r)
  else
    handleHealthChecksLogging(next, r)

  def handleLoggingAndTracing(
      next: RoutingContext => RouteResult[smithy4play.EndpointResult],
      r: RoutingContext
  ): RouteResult[smithy4play.EndpointResult] = {
    // Start Trace Span Root
    val request = r.requestHeader
    val span = OpentelemetryProvider.getTracer().spanBuilder(request.path).startSpan()
    val latencyRecorder = OpentelemetryProvider
      .getMeter()
      .histogramBuilder("task_latency")
      .setDescription("The task latency in milliseconds")
      .setUnit("ms")
      .build()

    var xTracingId = (XTRACINGID, span.getSpanContext.getTraceId)

    if (request.headers.get(XTRACINGID).isDefined)
      xTracingId = (XTRACINGID, request.headers.get(XTRACINGID).get)

    // Add Annotations and Attributes to newly created Root Trace
    span.makeCurrent()
    span.addEvent("TracingFilter")
    span.setAttribute("Position", "TracingFilter")
    span.setAttribute(SemanticAttributes.HTTP_URL, request.host + request.uri)
    span.setAttribute(SemanticAttributes.HTTP_HOST, request.host)
    span.setAttribute(SemanticAttributes.HTTP_METHOD, request.method)

    // Add new Span to internal Request
    val map = scala.collection.mutable.Map.empty[String, String]

    GlobalOpenTelemetry.getPropagators.getTextMapPropagator.inject(
      Context.current(),
      map,
      (carrier: scala.collection.mutable.Map[String, String], key: String, value: String) => {
        carrier.addOne((key, value))
      }
    )

    map.addOne(xTracingId)

    // Check Start Time
    val start = DateTime.now

    // Call Next Filter with new Headers
    val result = next(
      r.copy(
        requestHeader = request.withHeaders(request.headers.add(map.toList: _*))
      )
    )

    val logger = new TraceLogger(Some(span))
    val msg: String = logStart(request, xTracingId)(logger)

    // Process Result
    result.map { res =>
      // Add more Attributes to trace
      span.setAttribute(HTTP_STATUS_CODE, res.code)
      span.setAttribute(STATUS, res.code)
      span.setAttribute(HTTP_RESPONSE_SIZE, res.body.map(_.length).getOrElse(0))

      // Finish Root Span
      span.end()

      // Check end Time
      val end = DateTime.now

      // Add Metric with Span Processing Time
      latencyRecorder.record((end.getMillis - start.getMillis).toDouble)

      logResult(xTracingId, msg, res, request)(logger)

      // Add xTracingId to Result Header
      res.copy(
        headers = res.headers + xTracingId
      )
    }

  }

  private def handleHealthChecksLogging(
      next: RoutingContext => RouteResult[smithy4play.EndpointResult],
      r: RoutingContext
  ): RouteResult[smithy4play.EndpointResult] = {
    val result = next.apply(r)
    result.leftMap { e =>
      if (e.statusCode != 200) healthLogger.error(s"[HealthCheck] FAILED with ${e.statusCode}")
      e
    }
  }

  private def logStart(request: RequestHeader, xTracingId: (String, String))(logger: TraceLogger): String = {
    val authHeader = request.headers.get("Authorization")
    val header = if (authHeader.isDefined) authHeader.get.take(4) + "***" else "Not Set"
    val msg = s"method=${request.method} uri=${request.uri} authorization-header=$header"
    logger.info(s"[Trace-${xTracingId._2}] $msg START")
    msg
  }

  private def logResult(xTracingId: (String, String), msg: String, res: EndpointResult, req: RequestHeader)(
      logger: TraceLogger
  ): Unit =
    if (configAccessStatus.contains(res.code))
      logger.warn(s"[Trace-${xTracingId._2}] $msg status=${res.code} END")
    else
      logger.info(s"[Trace-${xTracingId._2}] $msg status=${res.code} END")
}

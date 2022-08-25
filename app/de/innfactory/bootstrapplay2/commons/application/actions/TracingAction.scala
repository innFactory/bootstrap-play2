package de.innfactory.bootstrapplay2.commons.application.actions

import de.innfactory.bootstrapplay2.commons.application.actions.models.RequestWithTrace
import de.innfactory.bootstrapplay2.commons.tracing.Common._
import de.innfactory.play.smithy4play.LogContext
import de.innfactory.play.tracing.TraceRequest
import io.opencensus.scala.Tracing.{startSpan, startSpanWithRemoteParent, traceWithParent}
import io.opencensus.trace._
import play.api.Environment
import play.api.mvc._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TracingAction @Inject() (
    val parser: BodyParsers.Default,
    implicit val environment: Environment
)(implicit val executionContext: ExecutionContext) {
  // def apply(traceString: String): TraceActionBuilder = new TraceActionBuilder(traceString, parser)
  def apply()(implicit logContext: LogContext): ActionBuilder[RequestWithTrace, AnyContent] = {
    val owningMethodName = Thread.currentThread.getStackTrace()(2).getMethodName
    val builder = new TraceActionBuilder(owningMethodName, parser)
    builder
  }
}

private[actions] class TraceActionBuilder(spanString: String, val parser: BodyParsers.Default)(implicit
    val executionContext: ExecutionContext
) extends ActionBuilder[RequestWithTrace, AnyContent] {

  def finishSpan[A](request: TraceRequest[A], result: Result, parentSpan: Span): Result = {
    request.traceSpan.end()
    parentSpan.end()
    result
  }

  override def invokeBlock[A](request: Request[A], block: RequestWithTrace[A] => Future[Result]): Future[Result] = {
    val optionalSpan: Option[_root_.io.opencensus.trace.Span] = generateSpanFromRemoteSpan(request)
    val span = optionalSpan.getOrElse(startSpan(spanString))
    traceWithParent(spanString, span) { spanChild =>
      createRequestWithSpanAndInvokeBlock(request, block, spanChild)
    }
  }

  private def createRequestWithSpanAndInvokeBlock[A](
      request: Request[A],
      block: RequestWithTrace[A] => Future[Result],
      span: Span
  ) = {
    val requestWithTrace = new RequestWithTrace(span, request)
    block(requestWithTrace).map { r =>
      finishSpan(requestWithTrace, r, span)
    }
  }

  private def generateSpanFromRemoteSpan[A](request: Request[A]) = {
    val headerTracingIdOptional = request.headers.get(XTRACINGID)
    val spanIdOptional = request.headers.get(X_INTERNAL_SPANID)
    val traceIdOptional = request.headers.get(X_INTERNAL_TRACEID)
    val traceOptionsOptional = request.headers.get(X_INTERNAL_TRACEOPTIONS)

    val span = for {
      headerTracingId <- headerTracingIdOptional
      spanId <- spanIdOptional
      traceId <- traceIdOptional
      traceOptions <- traceOptionsOptional
    } yield startSpanWithRemoteParent(
      headerTracingId,
      SpanContext.create(
        TraceId.fromLowerBase16(traceId),
        SpanId.fromLowerBase16(spanId),
        TraceOptions.fromLowerBase16(traceOptions, 0),
        Tracestate.builder().build()
      )
    )
    span
  }
}

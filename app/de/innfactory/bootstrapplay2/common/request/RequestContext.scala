package de.innfactory.bootstrapplay2.common.request

import de.innfactory.bootstrapplay2.actions.RequestWithCompany
import de.innfactory.bootstrapplay2.common.request.logger.TraceLogger
import de.innfactory.bootstrapplay2.models.api.Company
import de.innfactory.play.tracing.TraceRequest
import io.opencensus.trace.Span
import play.api.mvc.{ AnyContent, Request }

class TraceContext(traceSpan: Span) {
  def span: Span = traceSpan

  private val traceLogger = new TraceLogger(span)

  final def log: TraceLogger = traceLogger
}

trait BaseRequestContext {

  def request: Request[AnyContent]

}

trait RequestContextCompany[COMPANY] {
  def company: COMPANY
}

class RequestContext(rcSpan: Span, rcRequest: Request[AnyContent])
    extends TraceContext(rcSpan)
    with BaseRequestContext {
  override def request: Request[AnyContent] = rcRequest
}

case class RequestContextWithCompany(
  override val span: Span,
  override val request: Request[AnyContent],
  company: Company
) extends RequestContext(span, request)
    with RequestContextCompany[Company]

object RequestContextWithCompany {
  implicit def toRequestContext(requestContextWithUser: RequestContextWithCompany): RequestContext =
    new RequestContext(requestContextWithUser.span, requestContextWithUser.request)
}

object ReqConverterHelper {

  def requestContext[R[A] <: TraceRequest[AnyContent]](implicit req: R[_]): RequestContext =
    new RequestContext(req.traceSpan, req.request)

  def requestContextWithCompany[R[A] <: RequestWithCompany[AnyContent]](implicit req: R[_]): RequestContextWithCompany =
    RequestContextWithCompany(req.traceSpan, req.request, req.company)

}

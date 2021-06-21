package de.innfactory.bootstrapplay2.common.request

import de.innfactory.bootstrapplay2.actions.RequestWithFirebaseUser
import de.innfactory.bootstrapplay2.common.request.logger.TraceLogger
import de.innfactory.bootstrapplay2.services.firebase.models.FirebaseUser
import de.innfactory.play.tracing.TraceRequest
import io.opencensus.trace.Span
import play.api.mvc.{ AnyContent, Request }

abstract class BaseRequestContext {

  def request: Request[AnyContent]

  def span: Span

  private val traceLogger = new TraceLogger(span)

  final def log: TraceLogger = traceLogger

}

trait RequestContextUser[USER] {
  def user: USER
}

class RequestContext(rcSpan: Span, rcRequest: Request[AnyContent]) extends BaseRequestContext {
  override def request: Request[AnyContent] = rcRequest
  override def span: Span                   = rcSpan
}

case class RequestContextWithUser(
  override val span: Span,
  override val request: Request[AnyContent],
  user: FirebaseUser
) extends RequestContext(span, request)
    with RequestContextUser[FirebaseUser] {}

object RequestContextWithUser {
  implicit def toRequestContext(requestContextWithUser: RequestContextWithUser): RequestContext =
    new RequestContext(requestContextWithUser.span, requestContextWithUser.request)
}

object ReqConverterHelper {

  def requestContext[R[A] <: TraceRequest[AnyContent]](implicit req: R[_]): RequestContext =
    new RequestContext(req.traceSpan, req.request)

  def requestContextWithUser[R[A] <: RequestWithFirebaseUser[AnyContent]](implicit
    req: R[_]
  ): RequestContextWithUser =
    RequestContextWithUser(req.traceSpan, req.request, req.firebaseUser)

}

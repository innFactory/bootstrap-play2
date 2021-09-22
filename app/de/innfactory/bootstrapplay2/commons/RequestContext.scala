package de.innfactory.bootstrapplay2.commons

import de.innfactory.bootstrapplay2.commons.application.actions.models.RequestWithUser
import de.innfactory.bootstrapplay2.commons.logging.TraceLogger
import de.innfactory.bootstrapplay2.users.domain.models.User
import de.innfactory.play.tracing.TraceRequest
import io.opencensus.trace.Span

abstract class TraceContext {

  def span: Span

  private val traceLogger = new TraceLogger(span)

  final def log: TraceLogger = traceLogger

}

trait RequestContextUser[USER] {
  def user: USER
}

class RequestContext(rcSpan: Span, rcHeaders: Map[String, Seq[String]]) extends TraceContext {
  def headers: Map[String, Seq[String]] = rcHeaders
  override def span: Span               = rcSpan
}

case class RequestContextWithUser(
  override val span: Span,
  override val headers: Map[String, Seq[String]],
  user: User
) extends RequestContext(span, headers)
    with RequestContextUser[User] {}

object RequestContextWithUser {
  implicit def toRequestContext(requestContextWithUser: RequestContextWithUser): RequestContext =
    new RequestContext(requestContextWithUser.span, requestContextWithUser.headers)
}

object ReqConverterHelper {

  def requestContext(implicit req: TraceRequest[_]): RequestContext =
    new RequestContext(req.traceSpan, req.request.headers.toMap)

  def requestContextWithUser[Q](implicit
    req: RequestWithUser[Q]
  ): RequestContextWithUser =
    RequestContextWithUser(req.traceSpan, req.request.headers.toMap, req.user)

}

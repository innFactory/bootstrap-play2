package de.innfactory.bootstrapplay2.commons
import cats.implicits.toBifunctorOps
import de.innfactory.bootstrapplay2.users.domain.models.{User, UserId}
import de.innfactory.play.smithy4play.{HttpHeaders, TraceContext}
import io.opencensus.trace.Span
import org.joda.time.DateTime

import scala.util.Try

trait ApplicationTraceContext extends TraceContext {

  def timeOverride: Option[DateTime] = Try(
    httpHeaders.getHeader("x-app-datetime").map(DateTime.parse)
  ).toEither
    .leftMap(left => this.log.error("cannot parse x-app-time, because of error " + left.getMessage))
    .toOption
    .flatten
}

class RequestContext(
    rhttpHeaders: HttpHeaders,
    rSpan: Option[Span] = None
) extends ApplicationTraceContext {
  override def httpHeaders: HttpHeaders = rhttpHeaders
  override def span: Option[Span] = rSpan
}

object RequestContext {
  implicit def fromRequestContextWithUser(requestContextWithUser: RequestContextWithUser): RequestContext =
    new RequestContext(requestContextWithUser.httpHeaders, requestContextWithUser.span)

  def empty = new RequestContext(HttpHeaders(Map.empty))
}

case class RequestContextWithUser(
    override val httpHeaders: HttpHeaders,
    override val span: Option[Span],
    user: User
) extends RequestContext(httpHeaders, span)

object RequestContextWithUser {
  implicit def toUserId(requestContextWithUser: RequestContextWithUser): UserId = requestContextWithUser.user.userId
}

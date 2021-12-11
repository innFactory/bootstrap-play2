package de.innfactory.bootstrapplay2.commons.application.actions.models

import de.innfactory.bootstrapplay2.users.domain.models.User
import de.innfactory.play.tracing.TraceRequest
import io.opencensus.trace.Span
import play.api.mvc.{ Request, WrappedRequest }

class RequestWithUser[A](val user: User, val request: Request[A], implicit val traceSpan: Span)
    extends WrappedRequest[A](request)
    with TraceRequest[A]

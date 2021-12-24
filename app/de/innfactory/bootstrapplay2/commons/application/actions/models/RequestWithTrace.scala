package de.innfactory.bootstrapplay2.commons.application.actions.models

import de.innfactory.play.tracing.TraceRequest
import io.opencensus.trace.Span
import play.api.mvc.{Request, WrappedRequest}

class RequestWithTrace[A](val traceSpan: Span, val request: Request[A])
    extends WrappedRequest[A](request)
    with TraceRequest[A]

package de.innfactory.bootstrapplay2.common.tracing

object Common {
  val XTRACINGID              = "X-Tracing-ID"
  val X_INTERNAL_TRACEID      = "X-Internal-TraceId"
  val X_INTERNAL_SPANID       = "X-Internal-SpanId"
  val X_INTERNAL_TRACEOPTIONS = "X-Internal-TraceOption"

  object GoogleAttributes {
    val HTTP_STATUS_CODE   = "http/status_code"
    val STATUS             = "status"
    val HTTP_RESPONSE_SIZE = "/http/response/size"
    val HTTP_URL           = "/http/url"
    val HTTP_HOST          = "/http/host"
    val HTTP_METHOD        = "/http/method"
  }
}

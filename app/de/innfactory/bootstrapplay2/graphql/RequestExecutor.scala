package de.innfactory.bootstrapplay2.graphql

import com.typesafe.config.Config
import de.innfactory.bootstrapplay2.commons.RequestContext
import de.innfactory.bootstrapplay2.graphql.schema.SchemaDefinition
import de.innfactory.grapqhl.play.request.RequestExecutionBase
import de.innfactory.play.smithy4play.HttpHeaders
import de.innfactory.play.tracing.{TracerProvider, TracingHelper}
import io.opentelemetry.api.trace.Span
import play.api.mvc.{AnyContent, Request}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RequestExecutor @Inject() (implicit config: Config)
    extends RequestExecutionBase[GraphQLExecutionContext, ExecutionServices](SchemaDefinition.graphQLSchema) {
  override def contextBuilder(services: ExecutionServices, request: Request[AnyContent])(implicit
      ec: ExecutionContext
  ): GraphQLExecutionContext =
    GraphQLExecutionContext(
      request = request,
      ec = ec,
      companiesService = services.companiesService,
      locationsService = services.locationsService
    )
}

object RequestExecutor {
  implicit class EnhancedRequest(request: Request[AnyContent]) {
    def toRequestContextAndExecute[T](spanString: String, f: RequestContext => Future[T])(implicit
        ec: ExecutionContext
    ): Future[T] = {
      val parentSpan = TracingHelper.generateSpanFromRemoteSpan(HttpHeaders(request.headers.toMap))
      val processRequest = (child: Span, parent: Option[Span]) => {
        val rc = new RequestContext(HttpHeaders(request.headers.toMap), Some(child))
        val result = f(rc)
        result.map { r =>
          parent.foreach(_.end())
          r
        }
      }
      parentSpan match {
        case Some(parent) =>
          TracingHelper.traceWithParent(spanString, parent, child => processRequest(child, parentSpan))
        case None =>
          val child = TracerProvider.getTracer.spanBuilder(spanString).startSpan()
          processRequest(child, parentSpan)
      }
    }
  }
}

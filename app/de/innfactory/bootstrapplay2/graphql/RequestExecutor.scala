package de.innfactory.bootstrapplay2.graphql

import de.innfactory.bootstrapplay2.graphql.schema.SchemaDefinition
import de.innfactory.grapqhl.play.request.RequestExecutionBase
import play.api.mvc.{AnyContent, Request}

import scala.concurrent.ExecutionContext

class RequestExecutor
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

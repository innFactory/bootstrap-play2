package de.innfactory.bootstrapplay2.graphql

import de.innfactory.bootstrapplay2.graphql.schema.SchemaDefinition
import de.innfactory.grapqhl.play.request.RequestExecutionBase
import play.api.mvc.{ AnyContent, Request }

class RequestExecutor
    extends RequestExecutionBase[GraphQLExecutionContext, ExecutionServices](SchemaDefinition.graphQLSchema) {
  override def contextBuilder(services: ExecutionServices, request: Request[AnyContent]): GraphQLExecutionContext =
    GraphQLExecutionContext(
      request = request,
      companiesRepository = services.companiesRepository,
      locationsRepository = services.locationsRepository
    )
}

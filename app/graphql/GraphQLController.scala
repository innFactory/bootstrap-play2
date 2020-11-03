package graphql

import de.innfactory.grapqhl.play.controller.GraphQLControllerBase
import graphql.schema.SchemaDefinition
import javax.inject.{ Inject, Singleton }
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class GraphQLController @Inject() (
  cc: ControllerComponents,
  executionServices: ExecutionServices,
  requestExecutor: RequestExecutor
)(implicit ec: ExecutionContext)
    extends GraphQLControllerBase(cc)(
      executionServices,
      SchemaDefinition.graphQLSchema,
      (request: Request[AnyContent]) => Right(true),
      requestExecutor
    ) {}

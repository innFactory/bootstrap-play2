package graphql.schema.queries

import graphql.GraphQLExecutionContext
import graphql.schema.queries.Company.allCompanies
import sangria.schema.{ fields, ObjectType }

object QueryDefinition {
  val Query: ObjectType[GraphQLExecutionContext, Unit] = ObjectType(
    name = "Query",
    description = "Familotel API Queries",
    fields = fields[GraphQLExecutionContext, Unit](
      allCompanies
    )
  )
}

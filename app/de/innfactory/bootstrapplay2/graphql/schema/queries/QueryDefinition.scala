package de.innfactory.bootstrapplay2.graphql.schema.queries

import Company.allCompanies
import de.innfactory.bootstrapplay2.graphql.GraphQLExecutionContext
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

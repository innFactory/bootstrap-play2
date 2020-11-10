package de.innfactory.bootstrapplay2.graphql.schema.mutations

import de.innfactory.bootstrapplay2.graphql.GraphQLExecutionContext
import sangria.schema.{ fields, ObjectType }

object MutationDefinition {

  val Mutations: ObjectType[GraphQLExecutionContext, Unit] = ObjectType(
    name = "mutation",
    description = "Familotel API Mutations",
    fields = fields(
    )
  )

}

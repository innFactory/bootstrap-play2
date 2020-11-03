package graphql.schema.mutations

import graphql.GraphQLExecutionContext
import sangria.schema.{ fields, ObjectType }

object MutationDefinition {

  val Mutations: ObjectType[GraphQLExecutionContext, Unit] = ObjectType(
    name = "mutation",
    description = "Familotel API Mutations",
    fields = fields(
    )
  )

}

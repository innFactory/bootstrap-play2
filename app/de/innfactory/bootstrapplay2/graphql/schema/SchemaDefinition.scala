package de.innfactory.bootstrapplay2.graphql.schema

import de.innfactory.bootstrapplay2.graphql.GraphQLExecutionContext
import de.innfactory.grapqhl.sangria.resolvers.generic.CustomRootResolver
import de.innfactory.bootstrapplay2.graphql.schema.mutations.MutationDefinition.Mutations
import de.innfactory.bootstrapplay2.graphql.schema.queries.QueryDefinition.Query
import sangria.execution.deferred.DeferredResolver
import sangria.schema.Schema

/**
 * Defines a GraphQL schema for the current project
 */
object SchemaDefinition {

  // Resolvers (CustomRootResolver with DeferredResolver) and Fetchers
  val resolvers: DeferredResolver[GraphQLExecutionContext] = DeferredResolver.fetchersWithFallback(
    new CustomRootResolver(
      Map()
    )
  )

  val graphQLSchema: Schema[GraphQLExecutionContext, Unit] =
    Schema(
      Query,
      None,
      description = Some(
        "Schema for Bootstrap API "
      )
    )

}

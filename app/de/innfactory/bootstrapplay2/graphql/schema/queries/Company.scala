package de.innfactory.bootstrapplay2.graphql.schema.queries

import de.innfactory.bootstrapplay2.commons.RequestContext
import de.innfactory.bootstrapplay2.commons.implicits.RequestToRequestContextImplicit.EnhancedRequest
import de.innfactory.bootstrapplay2.graphql.GraphQLExecutionContext
import de.innfactory.bootstrapplay2.graphql.schema.models.Companies.CompanyType
import de.innfactory.bootstrapplay2.graphql.schema.models.Arguments.FilterArg
import sangria.schema.{Field, ListType}

object Company {
  val allCompanies: Field[GraphQLExecutionContext, Unit] = Field(
    "allCompanies",
    ListType(CompanyType),
    arguments = FilterArg :: Nil,
    resolve = ctx => {
      ctx.ctx.request.toRequestContextAndExecute(
        "allCompanies GraphQL",
        (rc: RequestContext) => ctx.ctx.companiesService.getAllForGraphQL(ctx arg FilterArg)(rc)
      )(ctx.ctx.ec)
    },
    description = Some("Bootstrap Filter API companies query. Query group by id")
  )

}

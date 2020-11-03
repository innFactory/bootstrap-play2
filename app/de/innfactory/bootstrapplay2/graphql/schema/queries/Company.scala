package de.innfactory.bootstrapplay2.graphql.schema.queries

import de.innfactory.bootstrapplay2.graphql.GraphQLExecutionContext
import de.innfactory.bootstrapplay2.graphql.schema.models.Companies.CompanyType
import sangria.schema.{ Field, ListType }

object Company {
  val allCompanies: Field[GraphQLExecutionContext, Unit] = Field(
    "allCompanies",
    ListType(CompanyType),
    arguments = Nil,
    resolve = ctx => ctx.ctx.companiesRepository.all(ctx.ctx.request),
    description = Some("Familotel Filter API hotels query. Query group by id")
  )

}

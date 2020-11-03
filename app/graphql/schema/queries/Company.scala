package graphql.schema.queries

import graphql.GraphQLExecutionContext
import graphql.schema.models.Companies.CompanyType
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

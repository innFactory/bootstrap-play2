package de.innfactory.bootstrapplay2.graphql.schema.models

import de.innfactory.bootstrapplay2.commons.RequestContext
import de.innfactory.bootstrapplay2.companies.domain.models.Company
import de.innfactory.bootstrapplay2.graphql.GraphQLExecutionContext
import de.innfactory.bootstrapplay2.graphql.RequestExecutor.EnhancedRequest
import sangria.macros.derive.{deriveObjectType, AddFields, ReplaceField}
import sangria.schema.{Field, ListType, ObjectType, StringType}
import de.innfactory.grapqhl.sangria.implicits.JsonScalarType._
import de.innfactory.grapqhl.sangria.implicits.JodaScalarType._

object Companies {
  val CompanyType: ObjectType[GraphQLExecutionContext, Company] = deriveObjectType(
    ReplaceField(
      fieldName = "id",
      field = Field(name = "id", fieldType = StringType, resolve = c => c.value.id.value)
    ),
    AddFields(
      Field(
        name = "subcompanies",
        resolve = ctx =>
          ctx.ctx.request.toRequestContextAndExecute(
            "allCompanies GraphQL",
            (rc: RequestContext) => ctx.ctx.companiesService.getAllForGraphQL(None)(rc)
          )(ctx.ctx.ec),
        fieldType = ListType(CompanyType)
      )
    )
  )
}

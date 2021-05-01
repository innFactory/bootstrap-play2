package de.innfactory.bootstrapplay2.graphql.schema.models

import de.innfactory.bootstrapplay2.common.filteroptions.FilterOptionUtils
import de.innfactory.bootstrapplay2.common.implicits.RequestToRequestContextImplicit.EnhancedRequest
import de.innfactory.bootstrapplay2.common.request.RequestContext
import de.innfactory.bootstrapplay2.graphql.GraphQLExecutionContext
import de.innfactory.bootstrapplay2.models.api.Company
import sangria.macros.derive.{ deriveObjectType, AddFields, ReplaceField }
import sangria.schema.{ Field, ListType, LongType, ObjectType, StringType }
import de.innfactory.grapqhl.sangria.implicits.JsonScalarType._
import de.innfactory.grapqhl.sangria.implicits.JodaScalarType._

object Companies {
  val CompanyType: ObjectType[GraphQLExecutionContext, Company] = deriveObjectType(
    ReplaceField(
      fieldName = "id",
      field = Field(name = "id", fieldType = StringType, resolve = c => c.value.id.get.toString)
    ),
    AddFields(
      Field(
        name = "subcompanies",
        resolve = ctx =>
          ctx.ctx.request.toRequestContextAndExecute(
            "allCompanies GraphQL",
            (rc: RequestContext) =>
              ctx.ctx.companiesRepository.allGraphQl(FilterOptionUtils.optionStringToFilterOptions(None))(rc)
          )(ctx.ctx.ec),
        fieldType = ListType(CompanyType)
      )
    )
  )
}

package de.innfactory.bootstrapplay2.graphql.schema.models

import de.innfactory.bootstrapplay2.graphql.GraphQLExecutionContext
import de.innfactory.bootstrapplay2.locations.domain.models.Location
import sangria.macros.derive.{ deriveObjectType, ReplaceField }
import sangria.schema.{ BigDecimalType, Field, ObjectType, OptionType, StringType }
import de.innfactory.grapqhl.sangria.implicits.JsonScalarType._
import de.innfactory.grapqhl.sangria.implicits.JodaScalarType._

object Locations {
  val LocationType: ObjectType[Unit, Location] = deriveObjectType(
    ReplaceField(
      fieldName = "id",
      field = Field(name = "id", fieldType = StringType, resolve = c => c.value.id.get.toString)
    ),
    ReplaceField(
      fieldName = "company",
      field = Field(name = "company", fieldType = StringType, resolve = c => c.value.company.toString)
    )
  )
}

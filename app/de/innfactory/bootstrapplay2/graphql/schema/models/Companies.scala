package de.innfactory.bootstrapplay2.graphql.schema.models

import de.innfactory.bootstrapplay2.models.api.Company
import sangria.macros.derive.{ deriveObjectType, ReplaceField }
import sangria.schema.{ Field, ObjectType, StringType }
import de.innfactory.grapqhl.sangria.implicits.JsonScalarType._
import de.innfactory.grapqhl.sangria.implicits.JodaScalarType._

object Companies {
  val CompanyType: ObjectType[Unit, Company] = deriveObjectType(
    ReplaceField(
      fieldName = "id",
      field = Field(name = "id", fieldType = StringType, resolve = c => c.value.id.get.toString)
    ),
  )
}

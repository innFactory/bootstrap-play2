package de.innfactory.bootstrapplay2.graphql.schema.models

import sangria.schema.{ Argument, OptionInputType, StringType }

object Arguments {
  val FilterArg: Argument[Option[String]] =
    Argument(
      "filter",
      OptionInputType(StringType),
      description = "Filters for companies, separated by & with key=value"
    )
}

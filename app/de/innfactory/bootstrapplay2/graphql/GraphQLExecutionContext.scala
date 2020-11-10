package de.innfactory.bootstrapplay2.graphql

import play.api.mvc.{ AnyContent, Request }
import de.innfactory.bootstrapplay2.repositories.{ CompaniesRepository, LocationRepository }

case class GraphQLExecutionContext(
  request: Request[AnyContent],
  companiesRepository: CompaniesRepository,
  locationsRepository: LocationRepository
)

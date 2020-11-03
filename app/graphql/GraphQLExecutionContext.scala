package graphql

import play.api.mvc.{ AnyContent, Request }
import repositories.{ CompaniesRepository, LocationRepository }

case class GraphQLExecutionContext(
  request: Request[AnyContent],
  companiesRepository: CompaniesRepository,
  locationsRepository: LocationRepository
)

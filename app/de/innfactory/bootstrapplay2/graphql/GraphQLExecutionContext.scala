package de.innfactory.bootstrapplay2.graphql

import play.api.mvc.{ AnyContent, Request }
import de.innfactory.bootstrapplay2.repositories.{ CompaniesRepository, LocationRepository }

import scala.concurrent.ExecutionContext

case class GraphQLExecutionContext(
  request: Request[AnyContent],
  ec: ExecutionContext,
  companiesRepository: CompaniesRepository,
  locationsRepository: LocationRepository
)

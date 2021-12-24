package de.innfactory.bootstrapplay2.graphql

import play.api.mvc.{AnyContent, Request}
import de.innfactory.bootstrapplay2.companies.domain.interfaces.CompanyService
import de.innfactory.bootstrapplay2.locations.domain.interfaces.LocationService

import scala.concurrent.ExecutionContext

case class GraphQLExecutionContext(
    request: Request[AnyContent],
    ec: ExecutionContext,
    companiesService: CompanyService,
    locationsService: LocationService
)

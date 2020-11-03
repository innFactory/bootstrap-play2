package graphql

import javax.inject.Inject
import repositories.{ CompaniesRepository, LocationRepository }

case class ExecutionServices @Inject() (
  companiesRepository: CompaniesRepository,
  locationsRepository: LocationRepository
)

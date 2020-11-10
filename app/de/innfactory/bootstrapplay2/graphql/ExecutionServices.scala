package de.innfactory.bootstrapplay2.graphql

import javax.inject.Inject
import de.innfactory.bootstrapplay2.repositories.{ CompaniesRepository, LocationRepository }

case class ExecutionServices @Inject() (
  companiesRepository: CompaniesRepository,
  locationsRepository: LocationRepository
)

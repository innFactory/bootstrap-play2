package de.innfactory.bootstrapplay2.graphql

import de.innfactory.bootstrapplay2.companies.domain.interfaces.CompanyService
import de.innfactory.bootstrapplay2.locations.domain.interfaces.LocationService

import javax.inject.Inject

case class ExecutionServices @Inject(
    companiesService: CompanyService,
    locationsService: LocationService
)

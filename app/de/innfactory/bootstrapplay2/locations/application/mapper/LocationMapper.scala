package de.innfactory.bootstrapplay2.locations.application.mapper

import de.innfactory.bootstrapplay2.application.controller.BaseMapper
import de.innfactory.bootstrapplay2.definition.{LocationRequestBody, LocationResponse, LocationsResponse}
import de.innfactory.bootstrapplay2.locations.domain.models.{Location, LocationCompanyId, LocationId}
import io.scalaland.chimney.dsl.TransformerOps

trait LocationMapper extends BaseMapper {
  implicit val locationToLocationResponse: Location => LocationResponse = (location: Location) =>
    location
      .into[LocationResponse]
      .withFieldComputed(_.id, l => l.id.get.value)
      .withFieldComputed(_.company, l => l.company.value)
      .withFieldComputed(_.created, l => dateTimeToDateWithTime(l.created.get))
      .withFieldComputed(_.updated, l => dateTimeToDateWithTime(l.updated.get))
      .transform

  implicit val locationsToLocationsResponse: Seq[Location] => LocationsResponse = (locations: Seq[Location]) =>
    LocationsResponse(locations.map(locationToLocationResponse))

  implicit val locationRequestBodyToLocation: LocationRequestBody => Location =
    (locationRequestBody: LocationRequestBody) =>
      locationRequestBody
        .into[Location]
        .withFieldComputed(_.id, l => l.id.map(LocationId))
        .withFieldComputed(_.company, l => LocationCompanyId(l.company))
        .withFieldConst(_.created, None)
        .withFieldConst(_.updated, None)
        .transform

}

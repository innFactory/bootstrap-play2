package de.innfactory.bootstrapplay2.locations.application.mapper

import de.innfactory.bootstrapplay2.application.controller.BaseMapper
import de.innfactory.bootstrapplay2.apidefinition.{LocationRequestBody, LocationResponse, LocationsResponse}
import de.innfactory.bootstrapplay2.locations.domain.models.{Location, LocationCompanyId, LocationId}
import io.scalaland.chimney.dsl.TransformerOps
import org.joda.time.DateTime

import java.util.UUID

trait LocationMapper extends BaseMapper {
  implicit val locationToLocationResponse: Location => LocationResponse = (location: Location) =>
    location
      .into[LocationResponse]
      .withFieldComputed(_.id, l => l.id.value)
      .withFieldComputed(_.company, l => l.company.value)
      .transform

  implicit val locationsToLocationsResponse: Seq[Location] => LocationsResponse = (locations: Seq[Location]) =>
    LocationsResponse(locations.map(locationToLocationResponse))

  implicit val locationRequestBodyToLocation: LocationRequestBody => Location =
    (locationRequestBody: LocationRequestBody) =>
      locationRequestBody
        .into[Location]
        .withFieldComputed(_.id, l => l.id.map(LocationId).getOrElse(UUID.randomUUID().toString))
        .withFieldComputed(_.company, l => LocationCompanyId(l.company))
        .withFieldConst(_.created, DateTime.now())
        .withFieldConst(_.updated, DateTime.now())
        .transform
}

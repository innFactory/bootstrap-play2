package de.innfactory.bootstrapplay2.locations.application.mapper

import de.innfactory.bootstrapplay2.application.controller.BaseMapper
import de.innfactory.bootstrapplay2.api.{LocationRequestBody, LocationResponse, LocationsResponse}
import de.innfactory.bootstrapplay2.companies.domain.models.CompanyId._
import de.innfactory.bootstrapplay2.locations.domain.models.{Location, LocationId}
import io.scalaland.chimney.dsl.TransformerOps
import org.joda.time.DateTime

trait LocationMapper extends BaseMapper {
  implicit val locationToLocationResponse: Location => LocationResponse = (location: Location) =>
    location
      .into[LocationResponse]
      .transform

  implicit val locationsToLocationsResponse: Seq[Location] => LocationsResponse = (locations: Seq[Location]) =>
    LocationsResponse(locations.map(locationToLocationResponse))

  implicit val locationRequestBodyToLocation: LocationRequestBody => Location =
    (locationRequestBody: LocationRequestBody) =>
      locationRequestBody
        .into[Location]
        .withFieldComputed(_.id, l => l.id.map(LocationId.locationIdToDomain).getOrElse(LocationId.create))
        .withFieldConst(_.created, DateTime.now())
        .withFieldConst(_.updated, DateTime.now())
        .transform
}

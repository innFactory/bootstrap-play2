package de.innfactory.bootstrapplay2.locations.domain.models

import de.innfactory.bootstrapplay2.api
import io.scalaland.chimney.Transformer

import java.util.UUID

case class LocationId(value: String)

object LocationId {
  def create: LocationId = LocationId(UUID.randomUUID().toString)

  implicit val locationIdFromDomain = (locationId: LocationId) =>
    de.innfactory.bootstrapplay2.api.LocationId(locationId.value)

  implicit val locationIdToDomain = (id: de.innfactory.bootstrapplay2.api.LocationId) => LocationId(id.value)

  implicit val locationIdFromDomainTransformer: Transformer[LocationId, de.innfactory.bootstrapplay2.api.LocationId] =
    locationIdFromDomain(_)
  implicit val locationIdToDomainTransformer: Transformer[de.innfactory.bootstrapplay2.api.LocationId, LocationId] =
    locationIdToDomain(_)
}

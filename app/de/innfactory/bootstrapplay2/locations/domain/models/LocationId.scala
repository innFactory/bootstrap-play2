package de.innfactory.bootstrapplay2.locations.domain.models

import java.util.UUID

case class LocationId(value: String)

object LocationId {
  def create: LocationId = LocationId(UUID.randomUUID().toString)
}

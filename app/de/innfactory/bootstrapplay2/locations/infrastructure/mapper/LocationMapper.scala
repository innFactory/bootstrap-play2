package de.innfactory.bootstrapplay2.locations.infrastructure.mapper

import dbdata.Tables
import de.innfactory.bootstrapplay2.locations.domain.models.{Location, LocationCompanyId, LocationId}
import io.scalaland.chimney.dsl.TransformerOps
import org.joda.time.DateTime

private[infrastructure] object LocationMapper {

  implicit def locationRowToLocations(row: Tables.LocationRow): Location =
    row
      .into[Location]
      .withFieldComputed[LocationId, LocationId](_.id, r => LocationId(r.id))
      .withFieldComputed[LocationCompanyId, LocationCompanyId](_.company, r => LocationCompanyId(r.company))
      .transform

  implicit def locationToLocationRow(location: Location): Tables.LocationRow =
    location
      .into[Tables.LocationRow]
      .withFieldComputed[String, String](_.id, c => c.id.value)
      .withFieldComputed[String, String](_.company, c => c.company.value)
      .transform

}

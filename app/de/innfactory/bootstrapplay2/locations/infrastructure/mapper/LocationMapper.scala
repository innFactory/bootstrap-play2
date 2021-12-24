package de.innfactory.bootstrapplay2.locations.infrastructure.mapper

import dbdata.Tables
import de.innfactory.bootstrapplay2.locations.domain.models.{Location, LocationCompanyId, LocationId}
import io.scalaland.chimney.dsl.TransformerOps
import org.joda.time.DateTime

private[infrastructure] object LocationMapper {

  implicit def locationRowToLocations(row: Tables.LocationRow): Location =
    row
      .into[Location]
      .withFieldComputed[Option[LocationId], Option[LocationId]](_.id, r => Some(LocationId(r.id)))
      .withFieldComputed[LocationCompanyId, LocationCompanyId](_.company, r => LocationCompanyId(r.company))
      .withFieldComputed[Option[DateTime], Option[DateTime]](_.created, c => Some(c.created))
      .withFieldComputed[Option[DateTime], Option[DateTime]](_.updated, c => Some(c.updated))
      .transform

  implicit def locationToLocationRow(location: Location): Tables.LocationRow =
    location
      .into[Tables.LocationRow]
      .withFieldComputed[Long, Long](_.id, c => c.id.map(_.value).getOrElse(0))
      .withFieldComputed[Long, Long](_.company, c => c.company.value)
      .withFieldComputed[DateTime, DateTime](_.created, c => c.created.getOrElse(DateTime.now()))
      .withFieldComputed[DateTime, DateTime](_.updated, c => c.updated.getOrElse(DateTime.now()))
      .transform

}

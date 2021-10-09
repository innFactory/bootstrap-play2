package de.innfactory.bootstrapplay2.companies.infrastructure.mapper

import dbdata.Tables
import de.innfactory.bootstrapplay2.companies.domain.models.{ Company, CompanyId }
import io.scalaland.chimney.dsl.TransformerOps
import org.joda.time.DateTime

private[infrastructure] object CompanyMapper {

  implicit def companyRowToCompany(row: Tables.CompanyRow): Company =
    row
      .into[Company]
      .withFieldComputed(_.id, r => Some(CompanyId(r.id)))
      .withFieldComputed[Option[DateTime], Option[DateTime]](_.created, c => Some(c.created))
      .withFieldComputed[Option[DateTime], Option[DateTime]](_.updated, c => Some(c.updated))
      .transform

  implicit def companyToCompanyRow(company: Company): Tables.CompanyRow =
    company
      .into[Tables.CompanyRow]
      .withFieldComputed[Long, Long](_.id, c => c.id.map(_.value).getOrElse(0))
      .withFieldComputed[DateTime, DateTime](_.created, c => c.created.getOrElse(DateTime.now()))
      .withFieldComputed[DateTime, DateTime](_.updated, c => c.updated.getOrElse(DateTime.now()))
      .transform

}

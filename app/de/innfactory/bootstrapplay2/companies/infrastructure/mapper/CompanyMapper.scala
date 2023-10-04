package de.innfactory.bootstrapplay2.companies.infrastructure.mapper

import dbdata.Tables
import de.innfactory.bootstrapplay2.companies.domain.models.{Company, CompanyId}
import io.scalaland.chimney.dsl.TransformerOps
import org.joda.time.DateTime

private[infrastructure] object CompanyMapper {

  implicit def companyRowToCompany(row: Tables.CompanyRow): Company =
    row
      .into[Company]
      .withFieldComputed(_.id, r => CompanyId(r.id))
      .transform

  implicit def companyToCompanyRow(company: Company): Tables.CompanyRow =
    company
      .into[Tables.CompanyRow]
      .withFieldComputed[String, String](_.id, c => c.id.value)
      .transform

}

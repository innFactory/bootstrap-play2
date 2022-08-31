package de.innfactory.bootstrapplay2.companies.application.mapper

import de.innfactory.bootstrapplay2.application.controller.BaseMapper
import de.innfactory.bootstrapplay2.companies.domain.models.{Company, CompanyId}
import de.innfactory.bootstrapplay2.api.{CompaniesResponse, CompanyRequestBody, CompanyResponse}
import io.scalaland.chimney.dsl.TransformerOps
import org.joda.time.DateTime

import java.util.UUID

trait CompanyMapper extends BaseMapper {
  implicit val companyToCompanyResponse: Company => CompanyResponse = (
    company: Company
  ) =>
    company
      .into[CompanyResponse]
      .withFieldComputed(_.id, c => c.id.value)
      .withFieldComputed(_.created, c => dateTimeToDateWithTime(c.created))
      .withFieldComputed(_.updated, c => dateTimeToDateWithTime(c.updated))
      .transform

  implicit val companiesToCompaniesResponse: Seq[Company] => CompaniesResponse = (companies: Seq[Company]) =>
    CompaniesResponse(companies.map(companyToCompanyResponse))

  implicit val companyRequestBodyToCompany: CompanyRequestBody => Company = (companyRequestBody: CompanyRequestBody) =>
    companyRequestBody
      .into[Company]
      .withFieldComputed(_.id, c => c.id.map(id => CompanyId(id)).getOrElse(CompanyId.create))
      .withFieldConst(_.created, DateTime.now())
      .withFieldConst(_.updated, DateTime.now())
      .transform
}

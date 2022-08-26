package de.innfactory.bootstrapplay2.companies.application.mapper

import de.innfactory.bootstrapplay2.application.controller.BaseMapper
import de.innfactory.bootstrapplay2.companies.domain.models.{Company, CompanyId}
import de.innfactory.bootstrapplay2.apidefinition.{CompaniesResponse, CompanyRequestBody, CompanyResponse}
import io.scalaland.chimney.dsl.TransformerOps

trait CompanyMapper extends BaseMapper {
  implicit val companyToCompanyResponse: Company => CompanyResponse = (
    company: Company
  ) =>
    company
      .into[CompanyResponse]
      .withFieldComputed(_.id, c => c.id.get.value)
      .withFieldComputed(_.created, c => dateTimeToDateWithTime(c.created.get))
      .withFieldComputed(_.updated, c => dateTimeToDateWithTime(c.updated.get))
      .transform

  implicit val companiesToCompaniesResponse: Seq[Company] => CompaniesResponse = (companies: Seq[Company]) =>
    CompaniesResponse(companies.map(companyToCompanyResponse))

  implicit val companyRequestBodyToCompany: CompanyRequestBody => Company = (companyRequestBody: CompanyRequestBody) =>
    companyRequestBody
      .into[Company]
      .withFieldComputed(_.id, c => c.id.map(CompanyId))
      .withFieldConst(_.created, None)
      .withFieldConst(_.updated, None)
      .transform
}

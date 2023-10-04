package de.innfactory.bootstrapplay2.companies.domain.models

import de.innfactory.bootstrapplay2.api
import io.scalaland.chimney.Transformer

import java.util.UUID

case class CompanyId(value: String)

object CompanyId {
  def create: CompanyId = CompanyId(UUID.randomUUID().toString)

  implicit val companyIdFromDomain = (companyId: CompanyId) =>
    de.innfactory.bootstrapplay2.api.CompanyId(companyId.value)

  implicit val companyIdToDomain = (id: de.innfactory.bootstrapplay2.api.CompanyId) => CompanyId(id.value)

  implicit val companyIdFromDomainTransformer: Transformer[CompanyId, de.innfactory.bootstrapplay2.api.CompanyId] =
    companyIdFromDomain(_)
  implicit val companyIdToDomainTransformer: Transformer[de.innfactory.bootstrapplay2.api.CompanyId, CompanyId] =
    companyIdToDomain(_)
}

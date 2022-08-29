package de.innfactory.bootstrapplay2.companies.domain.models

import java.util.UUID

case class CompanyId(value: String)

object CompanyId {
  def create: CompanyId = CompanyId(UUID.randomUUID().toString)
}

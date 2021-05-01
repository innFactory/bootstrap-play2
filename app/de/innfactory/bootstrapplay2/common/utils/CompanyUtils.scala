package de.innfactory.bootstrapplay2.common.utils

import java.util.UUID

import de.innfactory.bootstrapplay2.models.api.{ Company, Location }

case class CompanyAndLocation(company: Company, location: Location)

object IsCompanyOfLocation {
  def unapply(o: CompanyAndLocation): Boolean =
    if (o.company.id.isDefined && o.company.id.get.equals(o.location.company)) true
    else false
}

case class CompanyIdAndOldCompanyId(company: Company, companyId: UUID, companyIdOld: UUID)

object CompanyIdsAreEqual {
  def unapply(o: CompanyIdAndOldCompanyId): Boolean =
    if (
      o.company.id.isDefined && o.company.id.get.equals(o.companyId) && o.companyId
        .equals(
          o.companyIdOld
        )
    ) true
    else false
}

case class CompanyId(optionalCompany: Company, companyId: UUID)

object CompanyIdEqualsId {
  def unapply(o: CompanyId): Boolean =
    if (
      o.optionalCompany.id.isDefined && o.optionalCompany.id.get
        .equals(o.companyId)
    ) true
    else false
}

package de.innfactory.bootstrapplay2.common.utils

import java.util.UUID

import de.innfactory.bootstrapplay2.models.api.{ Company, Location }

case class CompanyAndLocation(company: Option[Company], location: Location)

object IsCompanyOfLocation {
  def unapply(o: CompanyAndLocation): Boolean =
    if (o.company.isDefined && o.company.get.id.isDefined && o.company.get.id.get.equals(o.location.company)) true
    else false
}

case class CompanyCompanyIdAndOldCompanyId(company: Option[Company], companyId: UUID, companyIdOld: UUID)

object CompanyIdsAreEqual {
  def unapply(o: CompanyCompanyIdAndOldCompanyId): Boolean =
    if (
      o.company.isDefined && o.company.get.id.isDefined && o.company.get.id.get.equals(o.companyId) && o.companyId
        .equals(
          o.companyIdOld
        )
    ) true
    else false
}

case class OptionAndCompanyId(optionalCompany: Option[Company], companyId: UUID)

object CompanyIdEqualsId {
  def unapply(o: OptionAndCompanyId): Boolean =
    if (
      o.optionalCompany.isDefined && o.optionalCompany.get.id.isDefined && o.optionalCompany.get.id.get
        .equals(o.companyId)
    ) true
    else false
}

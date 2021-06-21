package de.innfactory.bootstrapplay2.services.firebase.claims

object Claims {
  sealed trait Claim[T] {
    def key: String
  }

  sealed trait BooleanClaim extends Claim[Boolean]

  sealed trait LongClaim extends Claim[Long]

  sealed trait StringClaim extends Claim[String]

  case class InnFactoryAdmin() extends BooleanClaim {
    override def key: String = "innFactoryAdmin"
  }

  case class CompanyAdmin() extends LongClaim {
    override def key: String = "companyAdmin"
  }

}

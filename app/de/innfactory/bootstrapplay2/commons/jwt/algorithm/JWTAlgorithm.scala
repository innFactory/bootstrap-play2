package de.innfactory.bootstrapplay2.commons.jwt.algorithm

import com.nimbusds.jose.JWSAlgorithm

sealed trait JWTAlgorithm {
  def name: String
  def fullName: String
  def nimbusRepresentation: JWSAlgorithm
}

sealed trait JwtAsymmetricAlgorithm extends JWTAlgorithm           {}
sealed trait JwtRSAAlgorithm        extends JwtAsymmetricAlgorithm {}

object JWTAlgorithm {

  def fromString(algo: String): JWTAlgorithm = algo match {
    case "RS256" => RS256
    case "RS384" => RS384
    case "RS512" => RS512
    case _       => RS512
  }

  case object RS256 extends JwtRSAAlgorithm {
    def name = "RS256"; def fullName = "SHA256withRSA"; def nimbusRepresentation = JWSAlgorithm.RS256
  }
  case object RS384 extends JwtRSAAlgorithm {
    def name = "RS384"; def fullName = "SHA384withRSA"; def nimbusRepresentation = JWSAlgorithm.RS384
  }
  case object RS512 extends JwtRSAAlgorithm {
    def name = "RS512"; def fullName = "SHA512withRSA"; def nimbusRepresentation = JWSAlgorithm.RS512
  }
}

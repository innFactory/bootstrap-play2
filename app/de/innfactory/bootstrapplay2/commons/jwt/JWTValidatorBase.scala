package de.innfactory.bootstrapplay2.commons.jwt

import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.proc.BadJWTException
import de.innfactory.bootstrapplay2.commons.jwt.algorithm.JWTAlgorithm

abstract class JWTValidatorBase extends JWTValidator {

  val issuer: String

  val jwkSetSource: JWKSource[SecurityContext]

  private lazy val configurableJwtValidator =
    new ConfigurableJWTValidator(
      keySource = jwkSetSource,
      additionalValidations = List(),
      algorithm = JWTAlgorithm.RS512
    )

  override def validate(jwtToken: JWTToken): Either[BadJWTException, (JWTToken, JWTClaimsSet)] =
    configurableJwtValidator.validate(jwtToken)

}

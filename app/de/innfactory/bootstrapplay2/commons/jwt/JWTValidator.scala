package de.innfactory.bootstrapplay2.commons.jwt

import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.proc.BadJWTException
import de.innfactory.play.smithy4play.JWTToken

sealed abstract class ValidationError(message: String) extends BadJWTException(message)
case object EmptyJwtTokenContent extends ValidationError("Empty JWT token")
case object InvalidRemoteJwkSet extends ValidationError("Cannot retrieve remote JWK set")
case object InvalidJwtToken extends ValidationError("Invalid JWT token")
case object MissingExpirationClaim extends ValidationError("Missing `exp` claim")
case object InvalidTokenUseClaim extends ValidationError("Invalid `token_use` claim")
case object InvalidTokenIssuerClaim extends ValidationError("Invalid `iss` claim")
case object InvalidTokenSubject extends ValidationError("Invalid `sub` claim")
case object InvalidAudienceClaim extends ValidationError("Invalid `aud` claim")
case class UnknownException(exception: Exception) extends ValidationError(exception.getMessage)

trait JWTValidator {
  def validate(jwtToken: JWTToken): Either[BadJWTException, (JWTToken, JWTClaimsSet)]
}

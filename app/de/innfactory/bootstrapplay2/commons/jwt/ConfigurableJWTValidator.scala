package de.innfactory.bootstrapplay2.commons.jwt

import java.text.ParseException
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.{JWSVerificationKeySelector, SecurityContext}
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.proc.{BadJWTException, DefaultJWTClaimsVerifier, DefaultJWTProcessor}
import de.innfactory.bootstrapplay2.commons.jwt.algorithm.JWTAlgorithm
import de.innfactory.bootstrapplay2.commons.jwt.algorithm.JWTAlgorithm.RS256

object ConfigurableJWTValidator {
  def apply(
      keySource: JWKSource[SecurityContext],
      algorithm: JWTAlgorithm = RS256,
      maybeCtx: Option[SecurityContext] = None,
      additionalValidations: List[(JWTClaimsSet, SecurityContext) => Option[BadJWTException]] = List.empty
  ): ConfigurableJWTValidator = new ConfigurableJWTValidator(keySource, algorithm, maybeCtx, additionalValidations)
}

final class ConfigurableJWTValidator(
    keySource: JWKSource[SecurityContext],
    algorithm: JWTAlgorithm = RS256,
    maybeCtx: Option[SecurityContext] = None,
    additionalValidations: List[(JWTClaimsSet, SecurityContext) => Option[BadJWTException]] = List.empty
) extends JWTValidator {

  private val jwtProcessor = new DefaultJWTProcessor[SecurityContext]
  private val keySelector = new JWSVerificationKeySelector[SecurityContext](algorithm.nimbusRepresentation, keySource)
  jwtProcessor.setJWSKeySelector(keySelector)

  jwtProcessor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier[SecurityContext] {
    override def verify(claimsSet: JWTClaimsSet, context: SecurityContext): Unit = {
      super.verify(claimsSet, context)

      additionalValidations
        .to(LazyList)
        .map(f => f(claimsSet, context))
        .collect { case Some(e) => e }
        .foreach(e => throw e)
    }
  })

  private val ctx: SecurityContext = maybeCtx.orNull

  override def validate(jwtToken: JWTToken): Either[BadJWTException, (JWTToken, JWTClaimsSet)] = {
    val content: String = jwtToken.content
    if (content.isEmpty) Left(EmptyJwtTokenContent)
    else
      try {
        val claimsSet = jwtProcessor.process(content, ctx)
        Right(jwtToken -> claimsSet)
      } catch {
        case e: BadJWTException => Left(e)
        case _: ParseException  => Left(InvalidJwtToken)
        case e: Exception       => Left(UnknownException(e))
      }
  }
}

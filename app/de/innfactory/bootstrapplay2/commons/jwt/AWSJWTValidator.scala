package de.innfactory.bootstrapplay2.commons.jwt
import java.net.URL
import com.nimbusds.jose.jwk.source.{ JWKSource, RemoteJWKSet }
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jose.util.DefaultResourceRetriever
import de.innfactory.bootstrapplay2.commons.jwt.AWSJWTValidator.DEFAULT_HTTP_SIZE_LIMIT

object AWSJWTValidator {
  val DEFAULT_HTTP_SIZE_LIMIT: Int = 25 * 1024 * 1024
  def apply(
    url: JWKUrl
  ): AWSJWTValidator               = new AWSJWTValidator(url)
}

final class AWSJWTValidator(url: JWKUrl) extends JWTValidatorBase {

  val issuer: String = url.value

  val jwkSetSource: JWKSource[SecurityContext] = new RemoteJWKSet(
    new URL(s"${url.value}/.well-known/jwks.json"),
    new DefaultResourceRetriever(4000, 4000, DEFAULT_HTTP_SIZE_LIMIT)
  )

}

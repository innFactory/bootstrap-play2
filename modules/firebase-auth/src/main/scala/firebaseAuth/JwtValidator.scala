package firebaseAuth

import com.google.firebase.auth.{ FirebaseToken }
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.proc.BadJWTException

import scala.util.{ Failure, Success, Try }

final case class JwtToken(content: String) extends AnyVal

sealed abstract class ValidationError(message: String) extends BadJWTException(message)
case object EmptyJwtTokenContent                       extends ValidationError("Empty JWT token")
case object AutoInvalidByValidator                     extends ValidationError("auto-invalid")
case object InvalidJwtToken                            extends ValidationError("Invalid JWT token")
case object MissingExpirationClaim                     extends ValidationError("Missing `exp` claim")
case object InvalidTokenUseClaim                       extends ValidationError("Invalid `token_use` claim")
case object InvalidTokenIssuerClaim                    extends ValidationError("Invalid `iss` claim")
case object InvalidTokenSubject                        extends ValidationError("Invalid `sub` claim")
case class UnknownException(exception: Exception)      extends ValidationError("Unknown JWT validation error")

case class DefaultAuthResult(token: JwtToken, jwtClaimSet: JWTClaimsSet)

trait JwtValidator {
  def validate(jwtToken: JwtToken): Either[BadJWTException, Any]
}

class MockJWTValidator extends JwtValidator {
  override def validate(jwtToken: JwtToken): Either[BadJWTException, String] = {
    val decodedToken: Try[String] = {
      if (jwtToken.content == "VerifiedUser")
        Success("VerifiedUser")
      else
        Failure(new BadJWTException("failure"))
    }
    decodedToken match {
      case Success(dt) => Right(dt)
      case Failure(f)  => Left(new BadJWTException(f.getMessage, f))
    }
  }
}

package de.innfactory.bootstrapplay2.users.domain.models

import org.apache.commons.codec.binary.Base64
import org.joda.time.DateTime

import java.nio.charset.StandardCharsets
import java.security.{ MessageDigest, SecureRandom }
import scala.concurrent.duration.{ Duration, DurationInt }

case class UserPasswordResetToken(
  userId: UserId,
  token: String,
  created: org.joda.time.DateTime,
  validUntil: org.joda.time.DateTime
)

object UserPasswordResetToken {

  private val validity: Duration = 7.days

  def apply(userId: UserId): UserPasswordResetToken = {
    val encoded: String = createResetTokenString(userId)
    UserPasswordResetToken(
      userId,
      encoded,
      DateTime.now(),
      DateTime.now().plus(validity._1)
    )
  }

  private def createResetTokenString(userId: UserId) = {
    val random      = new SecureRandom()
    val randomBytes = random.generateSeed(10)
    val digest      = MessageDigest.getInstance("SHA-256")
    val hash        = digest.digest(userId.value.getBytes(StandardCharsets.UTF_8) ++ randomBytes)
    val encoded     = Base64.encodeBase64URLSafeString(hash)
    encoded
  }
}

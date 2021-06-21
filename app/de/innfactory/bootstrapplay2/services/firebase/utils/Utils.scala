package de.innfactory.bootstrapplay2.services.firebase.utils

import com.google.firebase.auth.UserRecord
import de.innfactory.bootstrapplay2.common.results.Results.Result
import de.innfactory.bootstrapplay2.common.results.errors.Errors.BadRequest
import de.innfactory.bootstrapplay2.services.firebase.claims.Claims.{
  BooleanClaim,
  Claim,
  CompanyAdmin,
  InnFactoryAdmin,
  LongClaim
}
import de.innfactory.bootstrapplay2.services.firebase.models.User
import de.innfactory.bootstrapplay2.services.firebase.models.{ FirebaseUser => InternalFirebaseUser }

object Utils {
  implicit class EnhancedUserRecord(userRecord: UserRecord) {
    def toUser: User = User.fromUserRecord(userRecord)

    def toFirebaseUser: InternalFirebaseUser = InternalFirebaseUser.fromUserRecord(userRecord)
  }

  implicit class EnhancedFirebaseUser(firebaseUser: InternalFirebaseUser) {

    def isInnFactoryAdmin: Boolean =
      firebaseUser.checkClaimsForClaim[Boolean, BooleanClaim](InnFactoryAdmin(), true)

    def isCompanyAdmin(companyAdmin: Long): Boolean =
      firebaseUser.checkClaimsForClaim[Long, LongClaim](
        CompanyAdmin(),
        companyAdmin
      )

    def getCompanyId(): Result[Long] =
      getClaimValueForClaim(CompanyAdmin())

    /**
     * Checks if one booleanClaim is true
     *
     * @param boolClaims List of BooleanClaims
     * @return
     */
    def checkForBooleanClaims(boolClaims: BooleanClaim*): Boolean = {
      val claims     = firebaseUser.getCustomClaims
      var claimMatch = false
      for (claim <- boolClaims)
        if (claims.containsKey(claim.key)) {
          val firebaseUserCustomClaim: Boolean = claims.get(claim.key).asInstanceOf[Boolean]
          if (firebaseUserCustomClaim.equals(true))
            claimMatch = firebaseUserCustomClaim
        }
      claimMatch
    }

    /**
     * Checks if claim equals expectedResult
     * @return
     */
    def checkClaimsForClaim[A, C <: Claim[A]](claim: C, expectedResult: A): Boolean = {
      val claims     = firebaseUser.getCustomClaims
      var claimMatch = false
      if (claims.containsKey(claim.key)) {
        val notInstanced            = claims.get(claim.key)
        val firebaseUserCustomClaim = notInstanced match {
          case b: java.math.BigDecimal => b.longValue
          case b: java.lang.String     => b
          case b: java.lang.Boolean    => b.asInstanceOf[Boolean]
        }
        if (firebaseUserCustomClaim == expectedResult) {
          claimMatch = true
        }
      }
      claimMatch
    }

    /**
     * Checks if claim equals expectedResult
     * @return
     */
    def getClaimValueForClaim[A, C <: Claim[A]](claim: C): Result[A] = {
      val claims = firebaseUser.getCustomClaims
      if (claims.containsKey(claim.key)) {
        val firebaseUserCustomClaim: A = claims.get(claim.key).asInstanceOf[A]
        Right(firebaseUserCustomClaim)
      } else
        Left(BadRequest())
    }

  }
}

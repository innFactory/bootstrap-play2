package de.innfactory.bootstrapplay2.services.firebase.models

import com.google.firebase.auth.UserRecord

case class FirebaseUser(
  uid: String,
  email: String,
  phoneNumber: String,
  emailVerified: Boolean,
  displayName: String,
  photoUrl: String,
  disabled: Boolean,
  customClaims: java.util.Map[java.lang.String, java.lang.Object],
  lastSignIn: Long,
  lastRefresh: Long,
  creation: Long
) {
  def getCustomClaims = customClaims
}

object FirebaseUser {
  implicit def fromUserRecord(userRecord: UserRecord): FirebaseUser =
    FirebaseUser(
      uid = userRecord.getUid,
      email = userRecord.getEmail,
      phoneNumber = userRecord.getPhoneNumber,
      emailVerified = userRecord.isEmailVerified,
      displayName = userRecord.getDisplayName,
      photoUrl = userRecord.getPhotoUrl,
      disabled = userRecord.isDisabled,
      customClaims = userRecord.getCustomClaims,
      lastSignIn = userRecord.getUserMetadata.getLastSignInTimestamp,
      lastRefresh = userRecord.getUserMetadata.getLastRefreshTimestamp,
      creation = userRecord.getUserMetadata.getCreationTimestamp
    )
}

name := "firebase-auth"

// JWT

val firebaseAdmin = "com.google.firebase" % "firebase-admin" % "6.15.0"
val nimbusJoseJWT = "com.nimbusds" % "nimbus-jose-jwt" % "8.20"

libraryDependencies ++= Seq(
  firebaseAdmin,
  nimbusJoseJWT
)
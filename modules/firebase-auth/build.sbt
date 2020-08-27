name := "firebase-auth"

// JWT

val firebaseAdmin = "com.google.firebase" % "firebase-admin" % "7.0.0"
val nimbusJoseJWT = "com.nimbusds" % "nimbus-jose-jwt" % "8.20"

libraryDependencies ++= Seq(
  firebaseAdmin,
  nimbusJoseJWT
)

skip in publish := true

name := "firebase-auth"

// JWT

val firebaseAdmin = "com.google.firebase" % "firebase-admin" % "7.0.0"
val nimbusJoseJWT = "com.nimbusds" % "nimbus-jose-jwt" % "9.0"

libraryDependencies ++= Seq(
  firebaseAdmin,
  nimbusJoseJWT
)

skip in publish := true

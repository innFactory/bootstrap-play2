name := "firebase-auth"

// JWT

val firebaseAdmin = "com.google.firebase" % "firebase-admin" % "7.0.1"
val nimbusJoseJWT = "com.nimbusds" % "nimbus-jose-jwt" % "9.1.2"

libraryDependencies ++= Seq(
  firebaseAdmin,
  nimbusJoseJWT
)

skip in publish := true

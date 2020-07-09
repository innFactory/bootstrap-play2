name := "firebase-auth"

// JWT
val firebaseAdmin = "com.google.firebase" % "firebase-admin" % "6.14.0"
val nimbusJoseJWT = "com.nimbusds" % "nimbus-jose-jwt" % "8.19"

libraryDependencies ++= Seq(
  firebaseAdmin,
  nimbusJoseJWT
)
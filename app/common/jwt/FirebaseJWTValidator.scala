package common.jwt
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.auth.{FirebaseAuth, FirebaseToken}
import com.google.firebase.{FirebaseApp, FirebaseOptions}
import com.nimbusds.jwt.proc.BadJWTException

import scala.util.{Failure, Success, Try}

object FirebaseJWTValidator {

  def instanciateFirebase(jsonFilePath: String, databseUrl: String) = {
    val serviceAccount =
      getClass().getClassLoader().getResourceAsStream(jsonFilePath)

    val options: FirebaseOptions = new FirebaseOptions.Builder()
      .setCredentials(GoogleCredentials.fromStream(serviceAccount))
      .setDatabaseUrl(databseUrl)
      .build
    FirebaseApp.initializeApp(options)
  }

  def deleteFirebase(): Unit = {
    FirebaseApp.getInstance().delete()
  }

  def apply(): FirebaseJWTValidator = new FirebaseJWTValidator()
}

class FirebaseJWTValidator extends JwtValidator[FirebaseToken] {

  override def validate(
                         jwtToken: JwtToken): Either[BadJWTException, FirebaseToken] = {
    val decodedToken = Try(
      FirebaseAuth.getInstance.verifyIdToken(jwtToken.content))
    decodedToken match {
      case Success(dt) => Right(dt)
      case Failure(f)  => Left(new BadJWTException(f.getMessage, f))
    }
  }
}
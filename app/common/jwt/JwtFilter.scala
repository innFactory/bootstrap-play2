package common.jwt

import akka.stream.Materializer
import com.nimbusds.jwt.proc.BadJWTException
import javax.inject.Inject
import play.api.mvc.Results.Unauthorized
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class JwtFilter @Inject() (implicit val mat: Materializer, ec: ExecutionContext) extends Filter {
  def apply(nextFilter: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {
    if(sys.env.getOrElse("SWAGGER_AUTH", "EXPOSE_SWAGGER") == "EXPOSE_SWAGGER" && requestHeader.path == "/v1/swagger.json") {
      nextFilter.apply(requestHeader)
    } else {
      requestHeader.headers.get("Authorization") match {
        case Some(header) =>{

          val token = header match {
            case x : String if x.startsWith("Bearer") => JwtToken(x.splitAt(7)._2)
            case x => JwtToken(x)
          }
          FirebaseJWTValidator.apply().validate(token) match {
            case Left(error: BadJWTException) => Future(Unauthorized(error.getMessage))
            case Right(dt) => nextFilter.apply(requestHeader)
          }
        }
        case None => Future.successful(
          Unauthorized("No authorization header present")
        )
      }
    }

  }
}
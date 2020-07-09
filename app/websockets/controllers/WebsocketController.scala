package websockets.controllers
import actorsystem.actors.commands.{ ResponseQueryHelloWorld, ResponseQueryHelloWorldError }
import actorsystem.services.HelloWorldService
import akka.actor.ActorSystem
import akka.stream.Materializer
import javax.inject.{ Inject, Singleton }
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import websockets.actors.WebSocketActor

import scala.concurrent.ExecutionContext

@Singleton
class WebsocketController @Inject() (
  cc: ControllerComponents
)(implicit ec: ExecutionContext, implicit val system: ActorSystem, mat: Materializer)
    extends AbstractController(cc) {

  def socket =
    WebSocket.accept[String, String] { request =>
      ActorFlow.actorRef { out =>
        WebSocketActor.props(out)
      }
    }

}

package de.innfactory.bootstrapplay2.websockets.controllers

import de.innfactory.bootstrapplay2.actorsystem.actors.commands.{
  ResponseQueryHelloWorld,
  ResponseQueryHelloWorldError
}
import de.innfactory.bootstrapplay2.actorsystem.services.HelloWorldService
import akka.actor.ActorSystem
import akka.stream.Materializer
import de.innfactory.bootstrapplay2.websockets.actors.WebSocketActor
import javax.inject.{ Inject, Singleton }
import play.api.libs.streams.ActorFlow
import play.api.mvc._

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

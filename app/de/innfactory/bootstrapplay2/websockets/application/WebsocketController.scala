package de.innfactory.bootstrapplay2.websockets.application

import akka.Done
import akka.actor.{ActorRef, ActorSystem}
import akka.stream.Materializer
import de.innfactory.bootstrapplay2.websockets.domain.interfaces.WebSocketService
import play.api.libs.json.JsValue

import akka.stream._
import akka.stream.scaladsl.Source
import de.innfactory.bootstrapplay2.websockets.infrastructure.actors.ScheduledActor
import play.api.http.ContentTypes
import play.api.libs.EventSource
import javax.inject.{Inject, Singleton}
import play.api.mvc.{WebSocket, _}
import scala.concurrent.ExecutionContext

@Singleton
class WebsocketController @Inject() (
    cc: ControllerComponents,
    webSocketService: WebSocketService
)(implicit ec: ExecutionContext, val system: ActorSystem, mat: Materializer)
    extends AbstractController(cc) {

  def socket =
    WebSocket.accept[JsValue, JsValue] { request =>
      webSocketService.socket
    }(WebSocket.MessageFlowTransformer.jsonMessageFlowTransformer)

  def serverSentEvents() = Action {
    val source: Source[Any, ActorRef] = Source.actorRef(
      completionMatcher = { case Done =>
        // complete stream immediately if we send it Done
        CompletionStrategy.immediately
      },
      // never fail the stream because of a message
      failureMatcher = PartialFunction.empty,
      bufferSize = 100,
      overflowStrategy = OverflowStrategy.dropHead
    )

    val pre = source.preMaterialize()

    val actorSource = pre._2.map {
      case msg: String => msg
      case _           => "Message"
    }
    system.actorOf(ScheduledActor.props(pre._1, 50, 250))

    val flow = actorSource via EventSource.flow

    flow.run()

    Ok.chunked(flow).as(ContentTypes.EVENT_STREAM)

  }

}

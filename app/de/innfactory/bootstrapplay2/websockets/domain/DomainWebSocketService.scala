package de.innfactory.bootstrapplay2.websockets.domain

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import de.innfactory.bootstrapplay2.actorsystem.domain.commands.Command
import de.innfactory.bootstrapplay2.websockets.domain.interfaces.{WebSocketRepository, WebSocketService}
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket

import javax.inject.Inject

class DomainWebSocketService @Inject(webSocketRepository: WebSocketRepository)(implicit
    val system: ActorSystem,
    mat: Materializer
) extends WebSocketService {
  override def socket: Flow[Any, Nothing, _] =
    ActorFlow.actorRef { out =>
      webSocketRepository.createWebSocketActor(out)
    }

}

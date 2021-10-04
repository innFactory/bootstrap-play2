package de.innfactory.bootstrapplay2.websockets.infrastructure.actors

import akka.actor.{ ActorRef, Props }
import de.innfactory.bootstrapplay2.websockets.domain.interfaces.WebSocketRepository

class WebSocketActorCreator extends WebSocketRepository {
  def createWebSocketActor(out: ActorRef): Props =
    WebSocketActor.props(out)
}

package de.innfactory.bootstrapplay2.websockets.domain.interfaces

import akka.actor.{ActorRef, Props}
import com.google.inject.ImplementedBy
import de.innfactory.bootstrapplay2.websockets.infrastructure.actors.WebSocketActorCreator

@ImplementedBy(classOf[WebSocketActorCreator])
trait WebSocketRepository {
  def createWebSocketActor(out: ActorRef): Props
}

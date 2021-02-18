package de.innfactory.bootstrapplay2.websockets.actors

import akka.actor._

object WebSocketActor {
  def props(out: ActorRef): Props = Props(new WebSocketActor(out))
}

class WebSocketActor(out: ActorRef) extends Actor {
  var counter                             = 1
  def receive: PartialFunction[Any, Unit] = { case msg: String =>
    out ! ("I received your message: " + msg + " | This is message: " + counter)
    counter += 1
  }
}

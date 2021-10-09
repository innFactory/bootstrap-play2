package de.innfactory.bootstrapplay2.websockets.infrastructure.actors

import akka.Done
import akka.actor._
import play.api.libs.json.JsValue

object ScheduledActor {
  def props(out: ActorRef, messageCount: Int, delay: Long): Props = Props(new ScheduledActor(out, messageCount, delay))
}
class ScheduledActor(out: ActorRef, messageCount: Int, delay: Long) extends Actor {
  var counter = 1

  def send(): Unit = {
    Thread.sleep(delay)
    out ! s"Test Message ${counter}"
    println(s"Sending $counter")
    counter = counter + 1
    if (counter < messageCount) {
      send()
    } else {
      out ! Done
    }
  }

  def receive: PartialFunction[Any, Unit] = { case _ =>
    println("Receive unknown")
  }

  send()
}

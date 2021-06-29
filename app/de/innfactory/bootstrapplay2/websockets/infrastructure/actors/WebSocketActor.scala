package de.innfactory.bootstrapplay2.websockets.infrastructure.actors

import akka.actor._
import play.api.libs.json.JsValue

object WebSocketActor {
  def props(out: ActorRef): Props = Props(new WebSocketActor(out))
}

case class Test(name: String, message: String)

import play.api.libs.json.Json
object Test {
  implicit val format = Json.format[Test]
}

class WebSocketActor(out: ActorRef) extends Actor {
  var counter                             = 1
  def receive: PartialFunction[Any, Unit] = {
    case jsValue: JsValue if (jsValue.validate[Test].asOpt.isDefined) =>
      out ! Json.toJson(
        Test(this.self.path.toString, "I received your message: " + jsValue + " | This is message: " + counter)
      )
      counter += 1
    case _                                                            => println("Receive unknown")
  }
}

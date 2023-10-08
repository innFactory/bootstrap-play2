package de.innfactory.bootstrapplay2.actorsystem.domain.services

import akka.actor._
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.scaladsl.adapter._
import akka.util.Timeout
import de.innfactory.bootstrapplay2.actorsystem.domain.actors.HelloWorldActor
import de.innfactory.bootstrapplay2.actorsystem.domain.commands.{Command, QueryHelloWorld, Response}
import de.innfactory.bootstrapplay2.actorsystem.domain.interfaces.HelloWorldService

import javax.inject._
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HelloWorldServiceImpl @Inject() (
)(implicit ec: ExecutionContext, system: ActorSystem)
    extends HelloWorldService {
  //  // asking someone requires a timeout if the timeout hits without response
  // the ask is failed with a TimeoutException
  private implicit val timeout: Timeout = 10.seconds

  // Convert classic actor system of play to typed
  private val actorSystem: akka.actor.typed.ActorSystem[_] = system.toTyped
  // define implicit scheduler
  private implicit val scheduler: akka.actor.typed.Scheduler =
    actorSystem.scheduler

  // spawn "root" supervisorActor on new typed actorSystem
  private val helloWorldActor: akka.actor.typed.ActorRef[Command] =
    actorSystem.systemActorOf(HelloWorldActor(), "helloWorldActor")

  def queryHelloWorld(query: String): Future[Response] = {
    val result: Future[Response] =
      helloWorldActor.ask((ref: akka.actor.typed.ActorRef[Response]) => QueryHelloWorld(query, ref))
    result
  }
}

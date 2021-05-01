package de.innfactory.bootstrapplay2.actorsystem.services

import de.innfactory.bootstrapplay2.actorsystem.actors.HelloWorldActor
import de.innfactory.bootstrapplay2.actorsystem.actors.commands.{ Command, QueryHelloWorld, Response }
import akka.actor._
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import com.google.inject.ImplementedBy
import javax.inject._
import akka.actor.typed.scaladsl.adapter._

import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future }

@ImplementedBy(classOf[HelloWorldServiceImpl])
trait HelloWorldService {
  def queryHelloWorld(query: String): Future[Response]
}

@Singleton
class HelloWorldServiceImpl @Inject() (
)(implicit ec: ExecutionContext, system: ActorSystem)
    extends HelloWorldService {
  //  // asking someone requires a timeout if the timeout hits without response
  // the ask is failed with a TimeoutException
  private implicit val timeout: Timeout = 10.seconds

  // Convert classic actor system of play to typed
  private val actorSystem: akka.actor.typed.ActorSystem[_]   = system.toTyped
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

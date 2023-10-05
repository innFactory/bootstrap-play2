package de.innfactory.bootstrapplay2.actorsharding.domain.common

import akka.actor.ActorSystem
import akka.actor.typed.scaladsl.adapter.ClassicActorSystemOps
import akka.cluster.sharding.typed.scaladsl.ClusterSharding
import akka.util.Timeout

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

@Singleton
class Sharding @Inject() (system: ActorSystem)(implicit ec: ExecutionContext) {

  implicit val timeout: Timeout = 10.seconds

  // Convert classic actor system of play to typed
  private val actorSystem: akka.actor.typed.ActorSystem[_] = system.toTyped

  private val scheduler: akka.actor.typed.Scheduler = actorSystem.scheduler

  private val sharding: ClusterSharding = ClusterSharding(actorSystem)

  def getSharding: ClusterSharding = sharding
  def getActorSystem: akka.actor.typed.ActorSystem[_] = actorSystem
  def getScheduler: akka.actor.typed.Scheduler = scheduler

}

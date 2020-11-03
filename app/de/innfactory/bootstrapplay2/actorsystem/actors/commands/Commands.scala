package de.innfactory.bootstrapplay2.actorsystem.actors.commands

import akka.actor.typed.ActorRef

// ############# GENERAL ###############

sealed trait Command
sealed trait Response

final case class QueryError(query: String, replyTo: ActorRef[Response]) extends Command

// ############# HELLO WORLD ###############

final case class QueryHelloWorld(query: String, replyTo: ActorRef[Response]) extends Command

case class ResponseQueryHelloWorld(query: String, answer: String)     extends Response
case class ResponseQueryHelloWorldError(query: String, error: String) extends Response

case class QueryHelloWorldResult(response: Response, replyTo: ActorRef[Response]) extends Command

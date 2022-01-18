package de.innfactory.bootstrapplay2.commons.implicits

import cats.data.EitherT

import scala.concurrent.{ExecutionContext, Future}

object EitherTF {
  def apply[A, B](toWrap: Either[A, B])(implicit ec: ExecutionContext): EitherT[Future, A, B] = EitherT(Future(toWrap))
}

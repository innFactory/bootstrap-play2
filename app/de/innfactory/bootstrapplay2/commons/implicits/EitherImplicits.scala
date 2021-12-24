package de.innfactory.bootstrapplay2.commons.implicits

import scala.concurrent.{ExecutionContext, Future}

object EitherImplicits {

  implicit class EitherFuture[A, B](value: Either[A, Future[B]]) {
    def foldEitherOfFuture(implicit ec: ExecutionContext): Future[Either[A, B]] =
      value match {
        case Left(s)  => Future.successful(Left(s))
        case Right(f) => f.map(Right(_))
      }
  }

}

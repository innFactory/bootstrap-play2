package common.utils

import common.results.Results.{ ErrorStatus, Result }

object OptionUtils {
  implicit class EnhancedOption[T](value: Option[T]) {
    def getOrElseOld(oldOption: Option[T]): Option[T] =
      value match {
        case Some(of) => Some(of)
        case None     => oldOption
      }

    def toEither(leftResult: ErrorStatus): Result[T] =
      value match {
        case Some(v) => Right(v)
        case None    => Left(leftResult)
      }

    def toInverseEither[X](leftResult: X): Either[X, String] =
      value match {
        case Some(_) => Left(leftResult)
        case None    => Right("")
      }
  }
}

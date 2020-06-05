package common.utils

object OptionUtils {
  implicit class EnhancedOption[T](value: Option[T]) {
    def getOrElseOld(oldOption: Option[T]): Option[T] =
      value match {
        case Some(of) => Some(of)
        case None     => oldOption
      }
  }
}

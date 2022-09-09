package common

object StringImplicit {
  implicit class EmptyStringOption(string: String) {
    def toOption: Option[String] = Option(string).filter(_.trim.nonEmpty)
  }
}

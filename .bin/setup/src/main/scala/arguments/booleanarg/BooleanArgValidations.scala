package arguments.booleanarg

object BooleanArgValidations {
  def isBooleanString(booleanString: String): Either[String, Boolean] = booleanString.toLowerCase match {
    case "y" | "yes" => Right(true)
    case "n" | "no"  => Right(false)
    case _           => Left("Invalid! Valid: y | yes | n | no")
  }
}

package arguments.stringarg

import cats.data.Validated

object StringArgValidations {
  val cantBeEmpty: String => Either[String, Unit] = (toValidate: String) =>
    Validated.cond(toValidate.nonEmpty, (), "Can't be empty!").toEither
  val onlyLetters: String => Either[String, Unit] = (toValidate: String) =>
    Validated.cond(toValidate.matches("^[a-zA-Z]+$"), (), "Can only consist out of letters!").toEither
}

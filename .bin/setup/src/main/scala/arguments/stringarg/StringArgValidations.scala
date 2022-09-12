package arguments.stringarg

import cats.data.Validated

object StringArgValidations {
  val cantBeEmpty: String => Either[String, Unit] = (toValidate: String) =>
    Validated.cond(toValidate.nonEmpty, (), "Can't be empty!").toEither
  val onlyLetters: String => Either[String, Unit] = (toValidate: String) =>
    Validated.cond(toValidate.matches("^[a-zA-Z]*?$"), (), "Can only consist out of letters!").toEither
  val onlyLettersNumbersHyphen: String => Either[String, Unit] = (toValidate: String) =>
    Validated
      .cond(
        toValidate.matches("^([a-zA-Z0-9]+(?:\\-?[a-zA-Z0-9]+))*?$"),
        (),
        "Can only consist out of letters and numbers separated by a single hyphen!"
      )
      .toEither
  val onlyLettersDot: String => Either[String, Unit] = (toValidate: String) =>
    Validated
      .cond(
        toValidate.matches("^([a-zA-Z]+(?:\\.[a-zA-Z]+))*?$"),
        (),
        "Can only consist out of letters separated by a single dot!"
      )
      .toEither
  val onlyLettersDotHyphen: String => Either[String, Unit] = (toValidate: String) =>
    Validated
      .cond(
        toValidate.matches("^([a-zA-Z]+(?:[\\.\\-]?[a-zA-Z]+))*?$"),
        (),
        "Can only consist out of letters separated by a single dot or hyphen!"
      )
      .toEither
}

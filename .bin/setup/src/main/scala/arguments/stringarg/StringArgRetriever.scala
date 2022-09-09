package arguments.stringarg

import scala.annotation.tailrec
import scala.io.StdIn.readLine

object StringArgRetriever {
  @tailrec
  def askFor(
      message: String,
      validations: Seq[String => Either[String, Unit]] =
        Seq(StringArgValidations.cantBeEmpty, StringArgValidations.onlyLetters)
  ): String = {
    println(message)
    val packageName = readLine().toLowerCase()
    validateInput(validations.map(validate => validate(packageName))) match {
      case Left(validationErrors) =>
        println(validationErrors)
        askFor(message, validations)
      case Right(_) => packageName
    }
  }

  private def validateInput(validations: Seq[Either[String, Unit]]) =
    validations.fold(Right(())) { (result, validation) =>
      validation match {
        case Left(validationError) =>
          result match {
            case Left(resultError) => Left(s"$resultError\n$validationError")
            case Right(_)          => Left(validationError)
          }
        case Right(_) => result
      }
    }
}

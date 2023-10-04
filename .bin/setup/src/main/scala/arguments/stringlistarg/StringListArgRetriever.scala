package arguments.stringlistarg

import scala.annotation.tailrec
import scala.io.StdIn.readLine

object StringListArgRetriever {
  @tailrec
  def askFor(
      message: String,
      validations: Seq[String => Either[String, Unit]] = Seq.empty
  ): Seq[String] = {
    println(message)
    val argsList = readLine().toLowerCase().split(" ").map(_.trim).filter(_.nonEmpty).toSeq
    val validationResults =
      argsList
        .flatMap(arg => validations.map(validate => validate(arg)))
        .fold(Right(())) { (result, current) =>
          result match {
            case Left(errorResult) =>
              current match {
                case Left(errorCurrent) => Left(errorResult + ". " + errorCurrent)
                case Right(_)           => Left(errorResult)
              }
            case Right(_) => current
          }
        }
    validationResults match {
      case Left(errors) =>
        println(errors)
        askFor(message, validations)
      case Right(_) => argsList
    }
  }
}

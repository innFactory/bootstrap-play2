package arguments.booleanarg

import scala.annotation.tailrec
import scala.io.StdIn.readLine

object BooleanArgRetriever {
  @tailrec
  def askFor(message: String): Boolean = {
    println(s"$message (y/n)")
    val is = readLine().toLowerCase()
    BooleanArgValidations.isBooleanString(is) match {
      case Left(error) =>
        println(error)
        askFor(message)
      case Right(boolean) => boolean
    }
  }
}

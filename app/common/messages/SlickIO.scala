package common.messages

object SlickIO {
  abstract class Error(message: String, statusCode: Int)

  case class SuccessWithStatusCode[T](body: T, status: Int)

  case class DatabaseError(message: String, statusCode: Int) extends Error(message = message, statusCode = statusCode)

  type Result[T] = Either[Error, T]

}

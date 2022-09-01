package actions.packagedomain

import actions.Action
import cats.data.Validated
import cats.data.Validated.Valid

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, OpenOption, Path, Paths}
import scala.annotation.tailrec
import scala.io.StdIn.readLine

case class PackageDomain() extends Action {
  override def keys: Seq[String] = PackageDomain.keys
  override def description: String = PackageDomain.description
}

object PackageDomain {
  private def keys: Seq[String] = Seq("-p", "--package")
  private def description: String = "Create a new package"

  def create() = {

    val packageName = askForPackage()
    val packageDomain = askForPackageDomain()

    println(s"Writing package into ${System.getProperty("user.dir")}...")

    Files.createDirectories(Path.of(s"${System.getProperty("user.dir")}/$packageName/application/mapper/"))
    Files.createDirectories(Path.of(s"${System.getProperty("user.dir")}/$packageName/domain/interfaces/"))
    Files.createDirectories(Path.of(s"${System.getProperty("user.dir")}/$packageName/domain/models/"))
    Files.createDirectories(Path.of(s"${System.getProperty("user.dir")}/$packageName/domain/services/"))
    Files.createDirectories(Path.of(s"${System.getProperty("user.dir")}/$packageName/infrastructure/mapper"))

    writeFile(s"$packageName/application/${packageDomain}Controller.scala", "")
    writeFile(s"$packageName/application/mapper/${packageDomain}Mapper.scala", "")
    writeFile(s"$packageName/domain/interfaces/${packageDomain}Repository.scala", "")
    writeFile(s"$packageName/domain/interfaces/${packageDomain}Service.scala", "")
    writeFile(s"$packageName/domain/models/${packageDomain}.scala", "")
    writeFile(s"$packageName/domain/models/${packageDomain}Id.scala", "")
    writeFile(s"$packageName/domain/services/Domain${packageDomain}Service.scala", "")
    writeFile(s"$packageName/infrastructure/Slick${packageDomain}Repository.scala", "")
    writeFile(s"$packageName/infrastructure/mapper/${packageDomain}Mapper.scala", "")

    println("Package written")
  }

  @tailrec
  private def askForPackage(): String = {
    println("Package name, e.g. companies")
    val packageName = readLine().toLowerCase()
    validateInput("Name of package", packageName) match {
      case Left(validationErrors) =>
        println(validationErrors)
        askForPackage()
      case Right(_) => packageName
    }
  }

  @tailrec
  private def askForPackageDomain(): String = {
    println("Package domain, e.g. company")
    val packageDomain = readLine().toLowerCase().capitalize
    validateInput("Name of package domain", packageDomain) match {
      case Left(validationErrors) =>
        println(validationErrors)
        askForPackageDomain()
      case Right(_) => packageDomain
    }
  }

  private def writeFile(path: String, content: String) =
    Files.write(Paths.get(System.getProperty("user.dir") + "/" + path), content.getBytes(StandardCharsets.UTF_8))

  private def validateInput(key: String, input: String) =
    Seq(
      Validated.cond(input.nonEmpty, (), s"$key can't be empty!").toEither,
      Validated.cond(input.matches("^[a-zA-Z]+$"), (), s"$key can only consist out of letters!").toEither
    ).fold(Right(())) { (result, validation) =>
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

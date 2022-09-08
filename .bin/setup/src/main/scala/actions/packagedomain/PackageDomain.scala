package actions.packagedomain

import actions.Action
import actions.packagedomain.domainfiles.scalafiles.{
  ApplicationController,
  ApplicationMapper,
  DomainModel,
  DomainModelId,
  DomainService,
  Repository,
  Service,
  SlickMapper,
  SlickRepository
}
import actions.packagedomain.domainfiles.smithyfiles.{ApiDefinition, ApiManifest}
import cats.data.Validated
import config.SetupConfig

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths, StandardOpenOption}
import scala.annotation.tailrec
import scala.io.StdIn.readLine
import scala.sys.process.Process

case class PackageDomain() extends Action {
  override def keys: Seq[String] = PackageDomain.keys
  override def description: String = PackageDomain.description
}

object PackageDomain {
  private def keys: Seq[String] = Seq("-p", "--package")
  private def description: String = "Create a new package"

  def create(packageNameArg: Option[String], packageDomainArg: Option[String], withCrudArg: Option[String])(implicit
      config: SetupConfig
  ): Unit = {
    val packageName = packageNameArg.getOrElse(askForPackage())
    val packageDomain = packageDomainArg.getOrElse(askForPackageDomain())
    val withCrud = (withCrudArg match {
      case Some(value) =>
        value.toLowerCase match {
          case "y" => Some(true)
          case "n" => Some(false)
          case _   => None
        }
      case None => None
    }).getOrElse(askForWithCrud())

    println(s"Writing package into ${System.getProperty("user.dir")}/")

    // package
    ApplicationController(packageDomain, packageName).writeDomainFile(withCrud)
    ApplicationMapper(packageDomain, packageName).writeDomainFile(withCrud)
    Repository(packageDomain, packageName).writeDomainFile(withCrud)
    Service(packageDomain, packageName).writeDomainFile(withCrud)
    DomainModel(packageDomain, packageName).writeDomainFile(withCrud)
    DomainModelId(packageDomain, packageName).writeDomainFile(withCrud)
    DomainService(packageDomain, packageName).writeDomainFile(withCrud)
    SlickRepository(packageDomain, packageName).writeDomainFile(withCrud)
    SlickMapper(packageDomain, packageName).writeDomainFile(withCrud)

    // smithy
    ApiDefinition(packageDomain, packageName).writeDomainFile(withCrud)
    ApiManifest(packageDomain, packageName).writeDomainFile(withCrud, Some(StandardOpenOption.APPEND))
    println(s"Done writing, compiling code...")
    Process("sbt compile").run()
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

  @tailrec
  private def askForWithCrud(): Boolean = {
    println("With crud (creade, read, update, delete)? (y/n)")
    val withCrud = readLine().toLowerCase()
    withCrud match {
      case "y" | "yes" => true
      case "n" | "no"  => false
      case _ =>
        println("Invalid answer!")
        askForWithCrud()
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

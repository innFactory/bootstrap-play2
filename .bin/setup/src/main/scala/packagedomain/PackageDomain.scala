package packagedomain

import packagedomain.scalafiles.{
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
import packagedomain.smithyfiles.{ApiDefinition, ApiManifest}
import arguments.booleanarg.{BooleanArgRetriever, BooleanArgValidations}
import arguments.stringarg.StringArgRetriever
import config.SetupConfig

import java.nio.file.StandardOpenOption
import scala.sys.process.Process

object PackageDomain {
  def create(packageNameArg: Option[String], packageDomainArg: Option[String], withCrudArg: Option[String])(implicit
      config: SetupConfig
  ): Unit = {
    val packageName = packageNameArg.getOrElse(StringArgRetriever.askFor("Package name, e.g. companies"))
    val packageDomain = packageDomainArg.getOrElse(StringArgRetriever.askFor("Package domain, e.g. company"))
    val withCrud = (withCrudArg match {
      case Some(value) =>
        BooleanArgValidations.isBooleanString(value) match {
          case Left(_)        => None
          case Right(boolean) => Some(boolean)
        }
      case None => None
    }).getOrElse(BooleanArgRetriever.askFor("With crud (create, read, update, delete)?"))

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
}

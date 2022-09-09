package arguments

import arguments.booleanarg.BooleanArgValidations
import cats.implicits.toBifunctorOps
import org.rogach.scallop.{ScallopConf, ScallopOption, Subcommand}

class Args(arguments: Seq[String]) extends ScallopConf(arguments) {
  object packageDomain extends Subcommand(Args.packageDomainKey) {
    val packageName: ScallopOption[String] = opt[String](descr = "Name of the package e.g. companies")
    val domain: ScallopOption[String] = opt[String](descr = "Name of the domain e.g. company")
    val withCrud: ScallopOption[String] =
      opt[String](
        name = "crud",
        short = 'c',
        descr = "(y/n) generates code for crud operations",
        validate = (booleanString: String) => BooleanArgValidations.isBooleanString(booleanString).isRight
      )
  }

  object bootstrap extends Subcommand(Args.bootstrapKey) {
    val projectSourcesRoot: ScallopOption[String] =
      opt[String](descr = "Folder name of the projects sources root, e.g. app")
    val projectPackagesRoot: ScallopOption[String] =
      opt[String](descr = "Folder name of the projects packages, e.g. de.innfactory.bootstrapplay2")
    val smithySourcesRoot: ScallopOption[String] =
      opt[String](descr = "Folder name of the smithy sources root, e.g. modules.api-definition")
    val smithyApiDefinitionRoot: ScallopOption[String] =
      opt[String](descr = "Folder name of the smithy declaration files, e.g. src.main.resources.META-INF.smithy")
  }

  addSubcommand(packageDomain)
  addSubcommand(bootstrap)
  verify()
}

object Args {
  val packageDomainKey = "package"
  val bootstrapKey = "bootstrap"
}

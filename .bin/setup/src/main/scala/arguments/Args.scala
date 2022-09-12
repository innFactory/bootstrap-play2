package arguments

import arguments.booleanarg.BooleanArgValidations
import arguments.stringarg.StringArgValidations
import org.rogach.scallop.{ScallopConf, ScallopOption, Subcommand}

class Args(arguments: Seq[String]) extends ScallopConf(arguments) {
  object packageDomain extends Subcommand(Args.packageDomainKey) {
    val packageName: ScallopOption[String] = opt[String](
      descr = "Name of the package e.g. companies",
      validate = (input: String) => StringArgValidations.onlyLetters(input).isRight
    )
    val domain: ScallopOption[String] = opt[String](
      descr = "Name of the domain e.g. company",
      validate = (input: String) => StringArgValidations.onlyLettersDot(input).isRight
    )
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
      opt[String](
        descr = "Folder name of the projects sources root, e.g. app",
        validate = (input: String) => StringArgValidations.onlyLetters(input).isRight
      )
    val projectName: ScallopOption[String] =
      opt[String](
        descr = "Name of the project e.g. bootstrap-play2",
        validate = (input: String) => StringArgValidations.onlyLettersNumbersHyphen(input).isRight
      )
    val projectDomain: ScallopOption[String] =
      opt[String](
        descr = "Folder name of the projects packages, e.g. de.innfactory",
        validate = (input: String) => StringArgValidations.onlyLettersDot(input).isRight
      )
    val smithySourcesRoot: ScallopOption[String] =
      opt[String](
        descr = "Folder name of the smithy sources root, e.g. modules.api-definition",
        validate = (input: String) => StringArgValidations.onlyLettersDotHyphen(input).isRight
      )
    val smithyApiDefinitionRoot: ScallopOption[String] =
      opt[String](
        descr = "Folder name of the smithy declaration files, e.g. src.main.resources.META-INF.smithy",
        validate = (input: String) => StringArgValidations.onlyLettersDotHyphen(input).isRight
      )
    val bootstrapPaths: ScallopOption[List[String]] = opt[List[String]](
      descr =
        "Paths of files and directories which shall be included during the bootstrap process, sources roots and build.sbt are always included",
      validate =
        (inputs: List[String]) => inputs.forall(input => StringArgValidations.onlyLettersDotSlash(input).isRight)
    )
  }
  addSubcommand(packageDomain)
  addSubcommand(bootstrap)
  verify()
}

object Args {
  val packageDomainKey = "package"
  val bootstrapKey = "bootstrap"
}

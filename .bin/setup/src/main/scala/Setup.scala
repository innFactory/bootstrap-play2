import Args.packageDomainKey
import actions.packagedomain.PackageDomain
import config.SetupConfig
import org.rogach.scallop.{ScallopConf, ScallopOption, Subcommand}

import java.nio.file.{Files, Paths}

object Setup extends App {

  implicit val config: SetupConfig = SetupConfig.get()
  val arguments = new Args(args)

  workingDirectoryIsProjectRoot() match {
    case Left(_) => println("Execute this script from your project root!")
    case Right(_) =>
      if (args.isEmpty) {
        arguments.printHelp()
      }
      if (args.contains(Args.packageDomainKey)) {
        PackageDomain.create(
          arguments.packageDomain.packageName.toOption,
          arguments.packageDomain.domain.toOption,
          arguments.packageDomain.withCrud.toOption
        )
      }
  }

  def workingDirectoryIsProjectRoot()(implicit config: SetupConfig) = {
    val filesInProjectRoot = Files.find(
      Paths.get(System.getProperty("user.dir")),
      1,
      (path, basicFileAttributes) => {
        val fileName = path.getFileName.toFile.getName
        basicFileAttributes.isDirectory && (fileName.matches(config.project.sourcesRoot) || fileName.matches(
          config.smithy.sourcesRoot
        ))
      }
    )
    if (filesInProjectRoot.count() == 2) Right(())
    else Left(())
  }
}

class Args(arguments: Seq[String]) extends ScallopConf(arguments) {
  object packageDomain extends Subcommand(packageDomainKey) {
    val packageName: ScallopOption[String] = opt[String](descr = "Name of the package e.g. companies")
    val domain: ScallopOption[String] = opt[String](descr = "Name of the domain e.g. company")
    val withCrud: ScallopOption[String] =
      opt[String](
        name = "crud",
        short = 'c',
        descr = "(y/n) generates code for crud operations"
      )
  }
  addSubcommand(packageDomain)
  verify()
}

object Args {
  val packageDomainKey = "package"
}

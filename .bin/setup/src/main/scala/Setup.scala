import packagedomain.PackageDomain
import arguments.Args
import bootstrap.Bootstrap
import cats.data.Validated
import config.SetupConfig
import config.SetupConfig.{BootstrapConfig, ProjectConfig, SmithyConfig}

import java.nio.file.{Files, Paths}

object Setup extends App {

  implicit val config: SetupConfig = SetupConfig.get()
  val arguments = new Args(args)

  workingDirectoryIsProjectRoot() match {
    case Left(_) =>
      println(
        s"Execute this script from your project root! Your project root must contain the directories ${config.project.sourcesRoot} and ${config.smithy.sourcesRoot}"
      )
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
      if (args.contains(Args.bootstrapKey)) {
        Bootstrap.init(
          SetupConfig(
            project = ProjectConfig(
              domain = arguments.bootstrap.projectDomain.toOption.getOrElse(""),
              name = arguments.bootstrap.projectName.toOption.getOrElse("")
            ),
            bootstrap = BootstrapConfig(
              paths = arguments.bootstrap.bootstrapPaths.toOption.getOrElse(Seq.empty)
            )
          )
        )
      }
  }

  def workingDirectoryIsProjectRoot()(implicit config: SetupConfig) =
    Validated
      .cond(
        Files.exists(Paths.get(System.getProperty("user.dir") + "/" + config.project.sourcesRoot.replace('.', '/'))) &&
          Files.exists(Paths.get(System.getProperty("user.dir") + "/" + config.smithy.sourcesRoot.replace('.', '/'))),
        (),
        ()
      )
      .toEither
}

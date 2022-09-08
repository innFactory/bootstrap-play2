import actions.Action
import actions.help.Help
import actions.packagedomain.PackageDomain
import config.SetupConfig

import java.nio.file.{Files, Paths}

object Setup extends App {

  implicit val config: SetupConfig = SetupConfig.get()

  workingDirectoryIsProjectRoot() match {
    case Left(_) => println("Execute this script from your project root!")
    case Right(_) =>
      args.length match {
        case 0 =>
          println("Missing argument!")
          Help.referToHelp()
        case 1 => handleArgs(args.head)
        case _ =>
          println("Too many arguments!")
          Help.referToHelp()
      }
  }

  def handleArgs(argumentKey: String)(implicit config: SetupConfig): Unit =
    Action.availableArguments.find(argument => argument.keys.contains(argumentKey)) match {
      case Some(argumentOfKey) =>
        argumentOfKey match {
          case _: Help          => Help.showHelp()
          case _: PackageDomain => PackageDomain.create()
          case _                => println("You found an unimplemented argument, report it!")
        }
      case None =>
        println("Unsupported argument!")
        Help.referToHelp()
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

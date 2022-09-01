import actions.Action
import actions.help.Help
import actions.packagedomain.PackageDomain

object Setup extends App {

  args.length match {
    case 0 =>
      println("Missing argument!")
      Help.referToHelp()
    case 1 => handleArgs(args.head)
    case _ =>
      println("Too many arguments!")
      Help.referToHelp()
  }

  def handleArgs(argumentKey: String): Unit =
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
}

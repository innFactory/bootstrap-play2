package actions.help

import actions.Action

case class Help() extends Action {
  override def keys: Seq[String] = Help.keys
  override def description: String = Help.description
}

object Help {
  private def keys: Seq[String] = Seq("-h", "--help")
  private def description: String = "List all available arguments"

  def showHelp(): Unit = Action.availableArguments.foreach { argument =>
    val keys = argument.keys.mkString(" | ")
    System.out.printf("%s \t %s \n", keys, argument.description)
  }

  def referToHelp(): Unit = println(s"Use ${keys.mkString(" or ")} for more information")
}

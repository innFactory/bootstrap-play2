package actions

import actions.packagedomain.PackageDomain
import actions.help.Help

trait Action {
  def keys: Seq[String]
  def description: String
}

object Action {
  val availableArguments = Seq(Help(), PackageDomain())
}

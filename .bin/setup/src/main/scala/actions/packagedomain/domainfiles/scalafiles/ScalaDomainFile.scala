package actions.packagedomain.domainfiles.scalafiles

import actions.packagedomain.domainfiles.DomainFile
import config.SetupConfig

trait ScalaDomainFile extends DomainFile {
  val fileEnding = "scala"

  override protected def getFullDirectoryPath()(implicit config: SetupConfig): String =
    s"${System.getProperty("user.dir")}/${config.project.getPackagePath()}${getAdjustedSubPath()}"
}

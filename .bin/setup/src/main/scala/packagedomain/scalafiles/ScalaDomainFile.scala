package packagedomain.scalafiles

import config.SetupConfig
import packagedomain.DomainFile

trait ScalaDomainFile extends DomainFile {
  val fileEnding = "scala"

  override protected def getFullDirectoryPath()(implicit config: SetupConfig): String =
    s"${System.getProperty("user.dir")}/${config.project.getPackagePath()}${getAdjustedSubPath()}"
}

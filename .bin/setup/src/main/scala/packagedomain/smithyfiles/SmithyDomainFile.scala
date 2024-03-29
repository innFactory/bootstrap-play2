package packagedomain.smithyfiles

import config.SetupConfig
import packagedomain.DomainFile

trait SmithyDomainFile extends DomainFile {
  val fileEnding = "smithy"
  val responseName: String = s"${packageDomain.capitalize}Response"
  val responsesName: String = s"${packageName.capitalize}Response"
  val requestBodyName: String = s"${packageDomain.capitalize}RequestBody"

  override protected def getFileName(): String =
    s"${nameLowerCased()}${if (fileEnding.nonEmpty) s".$fileEnding" else ""}"

  override protected def getFullDirectoryPath()(implicit config: SetupConfig): String =
    s"${System.getProperty("user.dir")}/${config.smithy.getPath}${getAdjustedSubPath()}"

}

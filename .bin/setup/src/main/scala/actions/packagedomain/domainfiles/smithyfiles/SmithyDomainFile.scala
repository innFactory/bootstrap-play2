package actions.packagedomain.domainfiles.smithyfiles

import actions.packagedomain.domainfiles.DomainFile
import config.SetupConfig

trait SmithyDomainFile extends DomainFile {
  val fileEnding = "smithy"
  val responseName: String = s"${packageDomain}Response"
  val responsesName: String = s"${packageName.capitalize}Response"
  val requestBodyName: String = s"${packageDomain}RequestBody"

  override protected def getFileName(): String =
    s"${nameLowerCased()}${if (fileEnding.nonEmpty) s".$fileEnding" else ""}"

  override protected def getFullDirectoryPath()(implicit config: SetupConfig): String =
    s"${System.getProperty("user.dir")}/${config.smithy.getPath}${getAdjustedSubPath()}"

}

package actions.packagedomain.domainfiles.smithyfiles

import actions.packagedomain.domainfiles.DomainFile

trait SmithyDomainFile extends DomainFile {
  val fileEnding = "smithy"
  val responseName: String = s"${packageDomain}Response"
  val responsesName: String = s"${packageName.capitalize}Response"
  val requestBodyName: String = s"${packageDomain}RequestBody"

  override protected def getFileName(): String = s"${nameLowerCased()}.${fileEnding}"

}

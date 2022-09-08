package actions.packagedomain.domainfiles.smithyfiles
import config.SetupConfig

case class ApiManifest(packageDomain: String, packageName: String) extends SmithyDomainFile {
  override def subPath: String = ""
  override def name: String = "manifest"

  override val fileEnding: String = ""

  override protected def getContent()(implicit config: SetupConfig): String = {
    val apiDefinition = ApiDefinition(packageDomain, packageName)
    s"\n${apiDefinition.nameLowerCased()}.${apiDefinition.fileEnding}"
  }
}

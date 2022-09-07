package actions.packagedomain.domainfiles

import config.SetupConfig

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}

trait DomainFile {
  def path()(implicit config: SetupConfig): String
  def name: String
  def packageDomain: String
  def packageName: String
  protected def fileEnding: String
  protected def getContent(): String

  protected def getFileName(): String = s"$name.${fileEnding}"

  def nameLowerCased(): String = Character.toLowerCase(name.charAt(0)) + name.substring(1)

  def writeDomainFile()(implicit config: SetupConfig): Unit = {
    val fullPath = path.last match {
      case '/' => s"${path}${getFileName()}"
      case _   => s"$path/${getFileName()}"
    }
    Files.createDirectories(Path.of(path))
    Files.write(Paths.get(fullPath), getContent().getBytes(StandardCharsets.UTF_8))
  }
}

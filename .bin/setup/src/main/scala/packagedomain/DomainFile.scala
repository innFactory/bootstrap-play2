package packagedomain

import config.SetupConfig

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, OpenOption, Path, Paths}

trait DomainFile {
  def subPath: String
  def name: String
  def packageDomain: String
  def packageName: String
  protected def fileEnding: String
  protected def getContent(withCrud: Boolean)(implicit config: SetupConfig): String

  protected def getFileName(): String = s"$name.$fileEnding"

  def nameLowerCased(): String = Character.toLowerCase(name.charAt(0)) + name.substring(1)

  protected def getAdjustedSubPath(): String = if (subPath.nonEmpty)
    (subPath.head, subPath.last) match {
      case ('/', '/') => s"$subPath"
      case ('/', _)   => s"$subPath/"
      case (_, '/')   => s"/$subPath"
      case (_, _)     => s"/$subPath/"
    }
  else "/"

  protected def getFullDirectoryPath()(implicit config: SetupConfig): String

  def writeDomainFile(withCrud: Boolean, openOptions: Option[OpenOption] = None)(implicit config: SetupConfig): Unit = {
    Files.createDirectories(Path.of(getFullDirectoryPath()))
    openOptions match {
      case Some(option) =>
        println(s"Writing ${getFileName()} into ${getFullDirectoryPath()} with option ${option.toString}")
        Files.write(
          Paths.get(getFullDirectoryPath() + getFileName()),
          getContent(withCrud).getBytes(StandardCharsets.UTF_8),
          option
        )
      case None =>
        println(s"Writing ${getFileName()} into ${getFullDirectoryPath()}")
        Files.write(
          Paths.get(getFullDirectoryPath() + getFileName()),
          getContent(withCrud).getBytes(StandardCharsets.UTF_8)
        )
    }
  }
}

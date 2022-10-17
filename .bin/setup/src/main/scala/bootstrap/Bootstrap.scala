package bootstrap

import arguments.stringarg.{StringArgRetriever, StringArgValidations}
import arguments.stringlistarg.StringListArgRetriever
import cats.implicits.toBifunctorOps
import common.SeqImplicit.EmptySeqOption
import common.StringImplicit.EmptyStringOption
import config.SetupConfig
import config.SetupConfig.{BootstrapConfig, ProjectConfig, SmithyConfig}
import play.api.libs.json.Json

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths, StandardCopyOption}
import scala.annotation.tailrec
import scala.sys.process.Process
import scala.util.Try

object Bootstrap {
  def init(configFromArgs: SetupConfig)(implicit config: SetupConfig): Unit = {
    val configForInit = askForMissingConfigs(configFromArgs)

    println(s"Running sbt clean...")
    Process("sbt clean").run()

    /**
     * Be careful when changing the order of the following actions, some depend on the original config to retrieve paths
     */
    if (config.project.getNamespace() != configForInit.project.getNamespace()) {
      println(s"Updating namespace from ${config.project.getNamespace()} to ${configForInit.project.getNamespace()}...")
      updateContent(config.project.getNamespace(), configForInit.project.getNamespace(), configForInit.bootstrap.paths)
    }
    if (config.project.name != configForInit.project.name) {
      println(s"Updating project name from ${config.project.name} to ${configForInit.project.name}...")
      updateContent(config.project.name, configForInit.project.name, configForInit.bootstrap.paths)
    }
    if (config.project.getPackagePath() != configForInit.project.getPackagePath()) {
      println(
        s"Updating packages path from ${config.project.getPackagePath()} to ${configForInit.project.getPackagePath()}..."
      )
      rename(config.project.getPackagePath(), configForInit.project.getPackagePath())
      deleteRecursivelyIfEmpty(config.project.getPackagePath().split("/").reverse.tail.reverse.mkString("/"))
    }

    println("Updating config...")
    updateConfig(configForInit)
    println("Running sbt compile")
    Process("sbt compile").run()
  }

  @tailrec
  private def deleteRecursivelyIfEmpty(path: String): Unit = {
    val file = Paths.get(path).toFile
    if (file.isDirectory && file.listFiles().length == 0) {
      file.delete()
      deleteRecursivelyIfEmpty(path.split("/").reverse.tail.reverse.mkString("/"))
    }
  }

  private def askForMissingConfigs(configFromArgs: SetupConfig)(implicit config: SetupConfig) =
    SetupConfig(
      project = ProjectConfig(
        domain = configFromArgs.project.domain.toOption
          .getOrElse(
            StringArgRetriever.askFor(
              "Folder name of the projects packages, default de.innfactory.bootstrapplay2",
              Seq(StringArgValidations.onlyLettersDot)
            )
          )
          .toOption
          .getOrElse(config.project.domain),
        name = configFromArgs.project.name.toOption.getOrElse(
          StringArgRetriever.askFor(
            "Name of the project e.g. bootstrap-play2",
            Seq(StringArgValidations.cantBeEmpty, StringArgValidations.onlyLettersNumbersHyphen)
          )
        )
      ),
      bootstrap = BootstrapConfig(
        paths = configFromArgs.bootstrap.paths.toOption.getOrElse(
          StringListArgRetriever
            .askFor(
              "Paths of files and directories which shall be included during the bootstrap process, sources roots and build.sbt are always included. Default: conf test .github",
              Seq(StringArgValidations.onlyLettersDotSlash)
            )
            .toOption
            .getOrElse(config.bootstrap.paths)
        )
      )
    )

  private def updateContent(old: String, next: String, bootstrapPaths: Seq[String])(implicit
      config: SetupConfig
  ): Unit = {
    updateBuildSbt(old, next)
    (bootstrapPaths :++ Seq(config.project.sourcesRoot.replace('.', '/'), config.smithy.sourcesRoot.replace('.', '/')))
      .foreach(path => updateContentInDirOrFile(path, old, next))
  }

  private def updateConfig(next: SetupConfig): Unit = {
    Files.createDirectories(Paths.get(SetupConfig.pathToProjectSetupConf))
    Files.write(
      SetupConfig.getFullPath(),
      Json.toJson(next).toString().getBytes(StandardCharsets.UTF_8)
    )
  }

  private def updateContentInDirOrFile(path: String, old: String, next: String): Unit = {
    val fileOrDir = Paths.get(s"${System.getProperty("user.dir")}/$path").toFile
    if (fileOrDir.isDirectory) {
      updateContentInDir(path, old, next)
    } else if (fileOrDir.isFile) {
      updateContentInFile(fileOrDir.toPath, old, next)
    }
  }

  private def updateContentInDir(pathOfDir: String, old: String, next: String): Unit = {
    val filesInDir = Files.list(Paths.get(pathOfDir))
    filesInDir.forEach { filePath =>
      val file = filePath.toFile
      if (file.isDirectory && file.getName != "target") {
        updateContentInDir(file.getPath, old, next)
      } else if (file.isFile) {
        updateContentInFile(filePath, old, next)
      } else {
        println(s"Unprocessed file or directory ${file.getPath}")
      }
    }
  }

  private def updateContentInFile(path: Path, old: String, next: String): Unit =
    if (path.toFile.getName.toLowerCase != ".ds_store") { // TODO get files to ignore from .gitignore??
      Try(
        Files.write(
          path,
          Files.readString(path).replaceAll(old, next).getBytes(StandardCharsets.UTF_8)
        )
      ).toEither.leftMap { error =>
        println(s"Error updating content in file $path")
        error.printStackTrace()
      }
    }

  private def rename(oldPath: String, nextPath: String): Unit = {
    Files.createDirectories(Path.of(s"${System.getProperty("user.dir")}/$nextPath"))
    Files.move(
      Paths
        .get(s"${System.getProperty("user.dir")}/$oldPath"),
      Paths
        .get(s"${System.getProperty("user.dir")}/$nextPath"),
      StandardCopyOption.ATOMIC_MOVE
    )
  }

  private def updateBuildSbt(old: String, next: String): Unit =
    updateContentInFile(Paths.get(s"${System.getProperty("user.dir")}/build.sbt"), old, next)
}

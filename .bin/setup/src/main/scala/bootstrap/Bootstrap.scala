package bootstrap

import arguments.stringarg.{StringArgRetriever, StringArgValidations}
import common.StringImplicit.EmptyStringOption
import config.SetupConfig
import config.SetupConfig.{ProjectConfig, SmithyConfig}
import play.api.libs.json.Json

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}
import scala.sys.process.Process

object Bootstrap {
  def init(configFromArgs: SetupConfig)(implicit config: SetupConfig): Unit = {
    val configForInit = askForMissingConfigs(configFromArgs)
    println(configForInit)

    println(s"Running sbt clean...")
    Process("sbt clean").run()

    /**
     * Be careful when changing the order of the following actions, some depend on the original config to retrieve paths
     */
    if (config.project.getNamespace() != configForInit.project.getNamespace()) {
      println(s"Updating namespace from ${config.project.getNamespace()} to ${configForInit.project.getNamespace()}...")
      updateNamespace(config.project.getNamespace(), configForInit.project.getNamespace())
    }
    if (config.project.name != configForInit.project.name) {
      println(s"Updating project name from ${config.project.name} to ${configForInit.project.name}...")
      updateProjectName(config.project.name, configForInit.project.name)
    }
    if (config.project.sourcesRoot != configForInit.project.sourcesRoot) {
      println(s"Updating sources root from ${config.project.sourcesRoot} to ${configForInit.project.sourcesRoot}...")
      updateSourcesRoot(config.project.sourcesRoot, configForInit.project.sourcesRoot)
    }
    if (config.smithy.apiDefinitionRoot != configForInit.smithy.apiDefinitionRoot) {
      println(s"Updating api definition root from ${config.smithy.apiDefinitionRoot
          .replace('.', '/')} to ${configForInit.smithy.apiDefinitionRoot.replace('.', '/')}...")
      updateApiDefinitionRoot(
        config.smithy.apiDefinitionRoot.replace('.', '/'),
        configForInit.smithy.apiDefinitionRoot.replace('.', '/')
      )
    }
    if (config.smithy.sourcesRoot != configForInit.smithy.sourcesRoot) {
      println(s"Updating smithy sources root from ${config.smithy.sourcesRoot
          .replace('.', '/')} to ${configForInit.smithy.sourcesRoot.replace('.', '/')}...")
      updateSourcesRoot(
        config.smithy.sourcesRoot.replace('.', '/'),
        configForInit.smithy.sourcesRoot.replace('.', '/')
      )
    }

    updateConfig(configForInit)
  }

  private def askForMissingConfigs(configFromArgs: SetupConfig)(implicit config: SetupConfig) =
    SetupConfig(
      project = ProjectConfig(
        sourcesRoot = configFromArgs.project.sourcesRoot.toOption
          .getOrElse(
            StringArgRetriever.askFor(
              "Folder name of the projects sources root, default app",
              Seq(StringArgValidations.onlyLetters)
            )
          )
          .toOption
          .getOrElse(config.project.sourcesRoot),
        domain = configFromArgs.project.domain.toOption
          .getOrElse(
            StringArgRetriever.askFor(
              "Folder name of the projects packages, default de.innfactory",
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
      smithy = SmithyConfig(
        sourcesRoot = configFromArgs.smithy.sourcesRoot.toOption
          .getOrElse(
            StringArgRetriever.askFor(
              "Folder name of the smithy sources root, default modules.api-definition",
              Seq(StringArgValidations.onlyLettersDotHyphen)
            )
          )
          .toOption
          .getOrElse(config.smithy.sourcesRoot),
        apiDefinitionRoot = configFromArgs.smithy.apiDefinitionRoot.toOption
          .getOrElse(
            StringArgRetriever.askFor(
              "Folder name of the smithy declaration files, default src.main.resources.META-INF.smithy",
              Seq(StringArgValidations.onlyLettersDotHyphen)
            )
          )
          .toOption
          .getOrElse(config.smithy.apiDefinitionRoot)
      )
    )

  private def updateNamespace(old: String, next: String)(implicit config: SetupConfig): Unit = {
    updateContentInDir(s"${System.getProperty("user.dir")}/${config.project.sourcesRoot}", old, next)
    updateContentInDir(s"${System.getProperty("user.dir")}/${config.smithy.getPath()}", old, next)
    updateContentInDir(s"${System.getProperty("user.dir")}/conf", old, next)
    val buildSbtPath = Paths.get(s"${System.getProperty("user.dir")}/build.sbt")
    Files.write(
      buildSbtPath,
      Files.readString(buildSbtPath).replaceAll(old, next).getBytes(StandardCharsets.UTF_8)
    )
  }

  private def updateSourcesRoot(old: String, next: String): Unit =
    rename(s"${System.getProperty("user.dir")}/$old", s"${System.getProperty("user.dir")}/$next")

  private def updateProjectName(old: String, next: String)(implicit config: SetupConfig): Unit = {
    updateContentInFile(Paths.get(s"${System.getProperty("user.dir")}/build.sbt"), old, next)
    updateContentInFile(
      Paths.get(s"${System.getProperty("user.dir")}/${config.project.sourcesRoot}/Module.scala"),
      old,
      next
    )
    updateContentInDir(s"${System.getProperty("user.dir")}/conf", old, next)
    updateContentInDir(s"${System.getProperty("user.dir")}/.github", old, next)
  }

  private def updateApiDefinitionRoot(old: String, next: String)(implicit config: SetupConfig): Unit =
    rename(
      s"${System.getProperty("user.dir")}/${config.smithy.sourcesRoot.replace('.', '/')}/$old",
      s"${System.getProperty("user.dir")}/${config.smithy.sourcesRoot.replace('.', '/')}/$next"
    )

  private def updateConfig(next: SetupConfig): Unit = {
    Files.createDirectories(Paths.get(SetupConfig.pathToProjectSetupConf))
    Files.write(
      SetupConfig.getFullPath(),
      Json.toJson(next).toString().getBytes(StandardCharsets.UTF_8)
    )
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
    Files.write(
      path,
      Files.readString(path).replaceAll(old, next).getBytes(StandardCharsets.UTF_8)
    )

  private def rename(oldPath: String, nextPath: String): Unit =
    Paths
      .get(oldPath)
      .toFile
      .renameTo(new File(nextPath))
}

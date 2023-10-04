import sbt.Credentials
import sbt.Keys.credentials
import sbtghpackages.GitHubPackagesPlugin.autoImport.{githubOwner, githubRepository}

object GithubConfig {
  private val token = sys.env.getOrElse("GITHUB_TOKEN", "")

  val settings = Seq(
    githubOwner := "innFactory",
    githubRepository := "bootstrap-play2",
    credentials :=
      Seq(
        Credentials(
          "GitHub Package Registry",
          "maven.pkg.github.com",
          "innFactory",
          token
        )
      )
  )
}

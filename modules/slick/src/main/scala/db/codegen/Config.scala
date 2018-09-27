package db.codegen

object Config  {
  lazy val databaseHost = sys.env.getOrElse("DATABASE_HOST", "localhost")
  lazy val databasePort = sys.env.getOrElse("DATABASE_PORT", "5432")
  lazy val databaseDb = sys.env.getOrElse("DATABASE_DB", "play")
  println(sys.env.get("DATABASE_DB"))
  lazy val databaseUser = sys.env.getOrElse("DATABASE_USER", "user")
  lazy val databasePassword = sys.env.getOrElse("DATABASE_PASSWORD", "password")
  lazy val databaseUrl = s"jdbc:postgresql://$databaseHost:$databasePort/$databaseDb?user=$databaseUser&password=$databasePassword"
  lazy val url = databaseUrl
  lazy val jdbcDriver =  "org.postgresql.Driver"
  lazy val slickProfile: XPostgresProfile = XPostgresProfile
}

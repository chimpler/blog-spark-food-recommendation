name := "blog-spark-recommendation"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  // HTTP client
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.0",
  // HTML parser
  "org.jodd" % "jodd-lagarto" % "3.5.2"
)     

play.Project.playScalaSettings

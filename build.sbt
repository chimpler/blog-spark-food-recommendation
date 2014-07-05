name := "blog-spark-recommendation"

version := "1.0-SNAPSHOT"

val sparkVersion = "1.0.0"

val akkaVersion = "2.2.3" // override Akka to be this version to match the one in Spark

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  // HTTP client
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.0",
  // HTML parser
  "org.jodd" % "jodd-lagarto" % "3.5.2",
  // Spark
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  // Akka
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
)

play.Project.playScalaSettings

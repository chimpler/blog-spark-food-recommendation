//import play.sbt.PlayImport._

//import play.sbt.PlayScala

name := "blog-spark-recommendation"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.7"

val sparkVersion = "1.6.1"

val akkaVersion = "2.4.7" // override Akka to be this version to match the one in Spark

lazy val `blogsparkrecommendation` = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  // HTTP client
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.3",
  // HTML parser
  "org.jodd" % "jodd-lagarto" % "3.7.1",
  // Spark
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  // Akka
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  // MongoDB
  "org.reactivemongo" %% "reactivemongo" % "0.11.14",
  // AWS
  "com.amazonaws" % "aws-java-sdk" % "1.0.002",
  // Conflict
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.7.4",
  "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.7.4"
)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
//resolvers += "RoundEights" at "http://maven.spikemark.net/roundeights"


//play.Project.playScalaSettings


//fork in run := true

//fork in run := true

fork in run := true
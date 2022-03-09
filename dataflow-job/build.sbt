import sbt._
import Keys._
import com.typesafe.sbt.packager.docker._

import scala.sys.process._
import complete.DefaultParsers._

import scala.language.postfixOps


val scioVersion = "0.11.5"
val beamVersion = "2.36.0"
val playJsonVersion = "1.39.2"
val gcpVersion = "1.115.5"

scalaVersion := "2.13.8"

name := "dataflow-job"

version := "0.1"

libraryDependencies ++= Seq(
  "com.spotify" %% "scio-core" % scioVersion,
  "com.spotify" %% "scio-google-cloud-platform" % scioVersion,
  "de.heikoseeberger" %% "akka-http-play-json" % playJsonVersion,
  "com.google.cloud" % "google-cloud-pubsub" % gcpVersion,
  "org.apache.beam" % "beam-runners-google-cloud-dataflow-java" % beamVersion
)

run / classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.Flat
run / fork := true

scalacOptions += "-Ymacro-annotations"

resolvers += "confluent" at "https://packages.confluent.io/maven/"

lazy val gcpProject = settingKey[String]("GCP Project")
lazy val gcpRegion = settingKey[String]("GCP region")
lazy val createFlextTemplate = inputKey[Unit]("create DataflowFlexTemplate")
lazy val runFlextTemplate = inputKey[Unit]("run DataflowFlexTemplate")

enablePlugins(JavaAppPackaging)

publish / skip := true

gcpProject := System.getenv("GOOGLE_CLOUD_PROJECT")
gcpRegion := "us-central1"

assembly / test := {}
assembly / assemblyJarName := "PubSubToBigQuery.jar"
assembly / assemblyMergeStrategy ~= { old =>
{
  case s if s.endsWith(".properties") => MergeStrategy.filterDistinctLines
  case s if s.endsWith("public-suffix-list.txt") =>
    MergeStrategy.filterDistinctLines
  case s if s.endsWith(".class") => MergeStrategy.last
  case s if s.endsWith(".proto") => MergeStrategy.last
  case s if s.endsWith("reflection-config.json") => MergeStrategy.rename
  case s if s.endsWith("config.fmpp") => MergeStrategy.discard
  case s                         => old(s)
}
}

Docker / packageName := s"gcr.io/${gcpProject.value}/dataflow/templates/PubSubToBigQuery"
Docker / dockerCommands := Seq(
  Cmd(
    "FROM",
    "gcr.io/dataflow-templates-base/java11-template-launcher-base:latest"
  ),
  Cmd(
    "ENV",
    "FLEX_TEMPLATE_JAVA_MAIN_CLASS",
    (assembly / mainClass).value.getOrElse("")
  ),
  Cmd(
    "ENV",
    "FLEX_TEMPLATE_JAVA_CLASSPATH",
    s"/template/${(assembly / assemblyJarName).value}"
  ),
  ExecCmd(
    "COPY",
    s"1/opt/docker/lib/${(assembly / assemblyJarName).value}",
    "${FLEX_TEMPLATE_JAVA_CLASSPATH}"
  )
)

Universal / mappings := {
  val fatJar = (Compile / assembly).value
  val filtered = (Universal / mappings).value.filter {
    case (_, name) => !name.endsWith(".jar")
  }
  filtered :+ (fatJar -> s"lib/${fatJar.getName}")
}
scriptClasspath := Seq((assembly / assemblyJarName).value)

createFlextTemplate := {
  val _ = (Docker / publish).value
  s"""gcloud beta dataflow DataflowFlexTemplate build
          gs://${gcpProject.value}/dataflow/templates/${name.value}.json
          --image ${dockerAlias.value}
          --sdk-language JAVA
          --metadata-file metadata.json""" !
}
runFlextTemplate := {
  val parameters = spaceDelimited("<arg>").parsed
  s"""gcloud beta dataflow DataflowFlexTemplate run ${name.value}
    	--template-file-gcs-location gs://${gcpProject.value}/dataflow/templates/${name.value}.json
    	--region=${gcpRegion.value}
    	--parameters ${parameters.mkString(",")}""" !
}
scalaVersion := "2.13.8"

name := "cloud-function"

version := "0.1"

libraryDependencies ++= Seq(
  "com.google.cloud.functions" % "functions-framework-api" % "1.0.4",
  "com.google.cloud" % "google-cloud-pubsub" % "1.115.5",
  "de.heikoseeberger" %% "akka-http-play-json" % "1.39.2"
)


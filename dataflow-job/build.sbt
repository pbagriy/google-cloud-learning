scalaVersion := "2.13.8"

name := "dataflow-job"

version := "0.1"

libraryDependencies ++= Seq(
  "com.spotify" %% "scio-core" % "0.11.5",
  "com.spotify" %% "scio-google-cloud-platform" % "0.11.5",
  "de.heikoseeberger" %% "akka-http-play-json" % "1.39.2",
  "com.google.cloud" % "google-cloud-pubsub" % "1.115.5",
  "org.apache.beam" % "beam-runners-google-cloud-dataflow-java" % "2.36.0"
)

//run / classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.Flat
run / fork := true

scalacOptions += "-Ymacro-annotations"

resolvers += "confluent" at "https://packages.confluent.io/maven/"

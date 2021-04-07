name := "kafka-playground"

version := "0.1"

//scalaVersion := "2.13.5"
scalaVersion := "2.11.8"

//"dev.zio" %% "zio-kafka" % "0.14.0"
libraryDependencies ++= Seq(
  "dev.zio" %% "zio-streams" % "1.0.2",
  "dev.zio" %% "zio-kafka" % "0.14.0",
  "org.slf4j" % "slf4j-log4j12" % "2.0.0-alpha1"
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => xs map {
    _.toLowerCase
  } match {
    case ("log4j-provider.properties" :: Nil) => MergeStrategy.first
    case _ => MergeStrategy.discard
  }
  case PathList("models", xs@_*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}
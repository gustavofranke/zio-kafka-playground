name := "kafka-playground"

version := "0.1"

//scalaVersion := "2.13.5"
scalaVersion := "2.11.8"

//"dev.zio" %% "zio-kafka" % "0.14.0"
libraryDependencies ++= Seq(
  "dev.zio" %% "zio-streams" % "1.0.2",
  "dev.zio" %% "zio-kafka"   % "0.14.0"
)
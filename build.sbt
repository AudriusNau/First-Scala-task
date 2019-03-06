name := """play-scala-slick-example"""

version := "2.7.x"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

libraryDependencies += guice
libraryDependencies += "com.typesafe.play" %% "play-slick" % "4.0.0"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "4.0.0"

libraryDependencies += "org.postgresql" % "postgresql" % "42.2.5"

libraryDependencies += specs2 % Test

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-Xfatal-warnings"
)

ThisBuild / organization := "com.sharonsyra"
ThisBuild / scalaVersion := "2.13.8"
ThisBuild / version := "1"
ThisBuild / name := "Todo"

lazy val akkaVersion = "2.6.19"
lazy val akkaHttpVersion = "10.2.9"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "org.scalatest" %% "scalatest" % "3.2.13" % "test",
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion
)

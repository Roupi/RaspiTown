lazy val scalatraVersion = "2.3.1"

lazy val root = (project in file(".")).settings(
  organization := "com.example",
  name := "scalatra-sbt-prototype",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.11.7",
  resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
  libraryDependencies ++= Seq(
    "org.scalatra"      %% "scalatra"          % scalatraVersion,
    "org.scalatra"      %% "scalatra-scalate"  % scalatraVersion,
    "org.scalatra"      %% "scalatra-specs2"   % scalatraVersion    % "test",
    "ch.qos.logback"    %  "logback-classic"   % "1.1.3"            % "runtime",
    "org.eclipse.jetty" %  "jetty-webapp"      % "9.2.10.v20150310" % "container",
    "javax.servlet"     %  "javax.servlet-api" % "3.1.0"            % "provided",
    "com.typesafe.akka" %% "akka-actor"   % "2.4.17"
  )
).settings(jetty(): _*)

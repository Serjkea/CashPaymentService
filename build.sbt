import cloudflow.sbt.CloudflowAkkaPlugin.AkkaVersion
import sbt._
import sbt.Keys._

lazy val cashPaymentService =  (project in file("."))
  .enablePlugins(CloudflowApplicationPlugin, CloudflowAkkaPlugin, CloudflowFlinkPlugin, CloudflowLibraryPlugin)
  .settings(
    scalaVersion := "2.12.11",
    runLocalConfigFile := Some("src/main/resources/local.conf"),
    name := "cash-payment-service-scala",
    schemaCodeGenerator := SchemaCodeGenerator.Scala,

    libraryDependencies ++= Seq(
      "com.lightbend.akka"     %% "akka-stream-alpakka-file"  % "1.1.2",
      "com.typesafe.akka"      %% "akka-http-spray-json"      % "10.1.12",
      "ch.qos.logback"         %  "logback-classic"           % "1.2.3",
      "com.typesafe.akka"      %% "akka-slf4j"                % AkkaVersion,
      "com.typesafe.akka"      %% "akka-http-testkit"         % "10.1.12" % "test",
      "org.scalatest"          %% "scalatest"                 % "3.0.8"  % "test"
    ),

    crossScalaVersions := Vector(scalaVersion.value),
    scalacOptions ++= Seq(
      "-encoding", "UTF-8",
      "-target:jvm-1.8",
      "-Xlog-reflective-calls",
      "-Xlint",
      "-Ywarn-unused",
      "-Ywarn-unused-import",
      "-deprecation",
      "-feature",
      "-language:_",
      "-unchecked"
    ),


    scalacOptions in (Compile, console) --= Seq("-Ywarn-unused", "-Ywarn-unused-import"),
    scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value

  )

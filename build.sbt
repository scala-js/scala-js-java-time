import sbt.Keys._
import org.scalajs.sbtplugin.cross.CrossProject

val commonSettings: Seq[Setting[_]] = Seq(
  version := "0.0.1-SNAPSHOT",
  organization := "org.scala-js",
  scalaVersion := "2.11.7",
  scalacOptions ++= Seq("-deprecation", "-feature", "-Xfatal-warnings"),
  homepage := Some(url("http://scala-js.org/")),
  licenses += ("MIT", url("http://opensource.org/licenses/mit-license.php"))
)

lazy val root: Project = project.in(file(".")).
  enablePlugins(ScalaJSPlugin).
  settings(commonSettings).
  settings(
    name := "scalajs-java-time"
  )

lazy val testSuite = CrossProject(
  jvmId = "testSuiteJVM",
  jsId = "testSuite",
  base = file("testSuite"),
  crossType = CrossType.Full
).
  jsConfigure(_ .enablePlugins(ScalaJSJUnitPlugin)).
  settings(commonSettings: _*).
  settings(
    testOptions +=
      Tests.Argument(TestFramework("com.novocode.junit.JUnitFramework"), "-v", "-a")
  ).
  jsSettings(
    name := "java.time testSuite on JS"
  ).
  jsConfigure(_.dependsOn(root)).
  jvmSettings(
    name := "java.time testSuite on JVM",
    libraryDependencies +=
      "com.novocode" % "junit-interface" % "0.9" % "test"
  )

lazy val testSuiteJS = testSuite.js
lazy val testSuiteJVM = testSuite.jvm

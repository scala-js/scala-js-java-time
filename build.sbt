import sbt.Keys._
import org.scalajs.sbtplugin.cross.CrossProject

lazy val root: Project = project.in(file(".")).
  enablePlugins(ScalaJSPlugin)

lazy val testSuite = CrossProject(
  jvmId = "testSuiteJVM",
  jsId = "testSuite",
  base = file("testSuite"),
  crossType = CrossType.Full
).
  jsConfigure(_ .enablePlugins(ScalaJSJUnitPlugin)).
  settings(
    testOptions +=
      Tests.Argument(TestFramework("com.novocode.junit.JUnitFramework"), "-v", "-a")
  ).
  jsSettings(
    name := "java.time testSuite on JS"
  ).
  jvmSettings(
    name := "java.time testSuite on JVM",
    libraryDependencies +=
      "com.novocode" % "junit-interface" % "0.9" % "test"
  )

lazy val testSuiteJS = testSuite.js.dependsOn(root)
lazy val testSuiteJVM = testSuite.jvm.dependsOn(root)

name := "scalajs-java-time"

version := "0.0.1-SNAPSHOT"

organization := "org.scala-js"

scalaVersion := "2.11.7"

scalacOptions ++= Seq("-deprecation", "-feature", "-Xfatal-warnings")

homepage := Some(url("http://scala-js.org/"))

licenses += ("MIT", url("http://opensource.org/licenses/mit-license.php"))

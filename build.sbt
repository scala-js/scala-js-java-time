import sbtcrossproject.crossProject

crossScalaVersions in ThisBuild := {
  val allVersions = Seq("2.12.10", "2.11.12", "2.10.7", "2.13.1")
  if (scalaJSVersion.startsWith("0.6."))
    allVersions
  else
    allVersions.filter(!_.startsWith("2.10."))
}
scalaVersion in ThisBuild := (crossScalaVersions in ThisBuild).value.head

val commonSettings: Seq[Setting[_]] = Seq(
  version := "1.0.1-SNAPSHOT",
  organization := "org.scala-js",
  scalacOptions ++= Seq("-deprecation", "-feature", "-Xfatal-warnings"),

  scalacOptions ++= {
    if (isSnapshot.value)
      Seq.empty
    else {
      val a = baseDirectory.value.toURI
      val g = "https://raw.githubusercontent.com/scala-js/scala-js-java-time"
      Seq(s"-P:scalajs:mapSourceURI:$a->$g/v${version.value}/")
    }
  },

  homepage := Some(url("http://scala-js.org/")),
  licenses += ("BSD New",
      url("https://github.com/scala-js/scala-js-java-time/blob/master/LICENSE")),
  scmInfo := Some(ScmInfo(
      url("https://github.com/scala-js/scala-js-java-time"),
      "scm:git:git@github.com:scala-js/scala-js-java-time.git",
      Some("scm:git:git@github.com:scala-js/scala-js-java-time.git")))
)

lazy val root: Project = project.in(file(".")).
  enablePlugins(ScalaJSPlugin).
  settings(commonSettings).
  settings(
    name := "scalajs-java-time",

    mappings in (Compile, packageBin) ~= {
      _.filter(!_._2.endsWith(".class"))
    },
    exportJars := true,

    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra := (
        <developers>
          <developer>
            <id>sjrd</id>
            <name>Sébastien Doeraene</name>
            <url>https://github.com/sjrd/</url>
          </developer>
          <developer>
            <id>gzm0</id>
            <name>Tobias Schlatter</name>
            <url>https://github.com/gzm0/</url>
          </developer>
          <developer>
            <id>nicolasstucki</id>
            <name>Nicolas Stucki</name>
            <url>https://github.com/nicolasstucki/</url>
          </developer>
        </developers>
    ),
    pomIncludeRepository := { _ => false }
  )

lazy val testSuite = crossProject(JSPlatform, JVMPlatform).
  jsConfigure(_ .enablePlugins(ScalaJSJUnitPlugin)).
  settings(commonSettings: _*).
  settings(
    testOptions +=
      Tests.Argument(TestFramework("com.novocode.junit.JUnitFramework"), "-v", "-a"),
    scalacOptions += "-target:jvm-1.8"
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

val scalaJSVersion =
  Option(System.getenv("SCALAJS_VERSION")).getOrElse("0.6.21")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % scalaJSVersion)

{
  if (scalaJSVersion != "1.0.0-M1")
    addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.3.0")
  else
    Nil
}

addSbtPlugin("org.scalastyle" % "scalastyle-sbt-plugin" % "0.8.0")

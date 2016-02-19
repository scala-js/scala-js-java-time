# scalajs-java-time

[![Build Status](https://travis-ci.org/scala-js/scala-js-java-time.svg?branch=master)](https://travis-ci.org/scala-js/scala-js-java-time)
[![Scala.js](https://www.scala-js.org/assets/badges/scalajs-0.6.6.svg)](https://www.scala-js.org/)

`scalajs-java-time` is a BSD-licensed reimplementation of the `java.time` API
of JDK8 for Scala.js. It enables this API in Scala.js projects.

## Usage

Simply add the following line to your sbt settings:

```scala
libraryDependencies += "org.scala-js" %%% "scalajs-java-time" % "0.1.0"
```

If you have a `crossProject`, the setting must be used only in the JS part:

```scala
lazy val myCross = crossProject.
  ...
  jsSettings.(
    libraryDependencies += "org.scala-js" %%% "scalajs-java-time" % "0.1.0"
  )
```

**Requirement**: you must use a host JDK8 to *build* your project, i.e., to
launch sbt. `scalajs-java-time` does not work on earlier JDKs.

**Note:**: the `parse` methods that appear in the JVM (Java 8) versions of the following classes have not yet been implemented in this library: `Period, LocalDate, MonthDay, Duration, LocalTime, Year and YearMonth` If you attempt to use one (in a custom uPickle pickler for example) you will get a linking error when transpiling to JavaScript due to the attempt to call a non-existent method.    

## License

`scalajs-java-time` is distributed under the
[BSD 3-Clause license](./LICENSE.txt).

## Contributing

Follow the [contributing guide](./CONTRIBUTING.md).

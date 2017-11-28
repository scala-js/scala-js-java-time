# scalajs-java-time

[![Build Status](https://travis-ci.org/scala-js/scala-js-java-time.svg?branch=master)](https://travis-ci.org/scala-js/scala-js-java-time)
[![Scala.js](https://www.scala-js.org/assets/badges/scalajs-0.6.17.svg)](https://www.scala-js.org/)

`scalajs-java-time` is a BSD-licensed reimplementation of the `java.time` API
of JDK8 for Scala.js. It enables this API in Scala.js projects.

## Usage

Simply add the following line to your sbt settings:

```scala
libraryDependencies += "org.scala-js" %%% "scalajs-java-time" % "0.2.3"
```

If you have a `crossProject`, the setting must be used only in the JS part:

```scala
lazy val myCross = crossProject.
  ...
  jsSettings.(
    libraryDependencies += "org.scala-js" %%% "scalajs-java-time" % "0.2.3"
  )
```

**Requirement**: you must use a host JDK8 to *build* your project, i.e., to
launch sbt. `scalajs-java-time` does not work on earlier JDKs.

## Work in Progress / linking errors

This library is a work in progress.
There are still many classes and methods that have not been implemented yet.
If you use any of those, you will get linking errors.

Feel free to [contribute](./CONTRIBUTING.md) to extend the set of supported
classes and methods!

## License

`scalajs-java-time` is distributed under the
[BSD 3-Clause license](./LICENSE.txt).

## Contributing

Follow the [contributing guide](./CONTRIBUTING.md).

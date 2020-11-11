# scalajs-java-time (deprecated)

[![Build Status](https://travis-ci.org/scala-js/scala-js-java-time.svg?branch=master)](https://travis-ci.org/scala-js/scala-js-java-time)
[![Scala.js](https://www.scala-js.org/assets/badges/scalajs-0.6.29.svg)](https://www.scala-js.org/)
[![Scala.js](https://www.scala-js.org/assets/badges/scalajs-1.0.0.svg)](https://www.scala-js.org)

`scalajs-java-time` is a BSD-licensed partial reimplementation of the `java.time` API of JDK8 for Scala.js.

## This project is incomplete and has stalled

This project is very incomplete and efforts to expand it have stalled.
Consider using one of the following alternatives instead:

* [scala-java-time](https://github.com/cquiroz/scala-java-time) (recommended): like this project, but better
* [scalajs-jsjoda](https://github.com/zoepepper/scalajs-jsjoda): implementation of `java.time` on top of the JavaScript library [js-joda](https://github.com/js-joda/js-joda)

Issues will *not* be worked on.

Pull requests *may* be reviewed and merged.

## Usage

Add the following line to your sbt settings:

```scala
libraryDependencies += "org.scala-js" %%% "scalajs-java-time" % "1.0.0"
```

If you have a `crossProject`, the setting must be used only in the JS part:

```scala
lazy val myCross = crossProject
  ...
  .jsSettings(
    libraryDependencies += "org.scala-js" %%% "scalajs-java-time" % "1.0.0"
  )
```

**Requirement**: you must use a host JDK8 to *build* your project, i.e., to
launch sbt. `scalajs-java-time` does not work on earlier JDKs.

## Linking errors

This library is very incomplete.
There are many classes and methods that have not been implemented yet.
If you use any of those, you will get linking errors.

Consider using on the alternatives mentioned above.

## License

`scalajs-java-time` is distributed under the
[BSD 3-Clause license](./LICENSE.txt).

## Contributing

Follow the [contributing guide](./CONTRIBUTING.md).

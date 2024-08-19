resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.6.3")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.16.0")
addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.21.1+36-2d9cbce2-SNAPSHOT")
addSbtPlugin("com.github.reibitto" % "sbt-welcome" % "0.4.0")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.12.1")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.2")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.3.1")
addSbtPlugin("org.typelevel" % "sbt-typelevel" % "0.7.2")
addSbtPlugin("org.typelevel" % "sbt-typelevel-site" % "0.7.1")
libraryDependencies += "org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.1.0"

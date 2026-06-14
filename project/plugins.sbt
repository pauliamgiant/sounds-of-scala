resolvers += Resolver.sonatypeCentralSnapshots

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.6.4")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.21.0")
addSbtPlugin("com.github.reibitto" % "sbt-welcome" % "0.5.0")
addSbtPlugin("org.typelevel" % "sbt-typelevel-settings" % "0.8.6")
addSbtPlugin("org.typelevel" % "sbt-typelevel-ci-release" % "0.8.6")
addSbtPlugin("org.typelevel" % "sbt-typelevel-site" % "0.8.6")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.6.1")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.14.6")
addSbtPlugin("de.heikoseeberger" % "sbt-header" % "5.10.0")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.3.2")
libraryDependencies += "org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.1.1"

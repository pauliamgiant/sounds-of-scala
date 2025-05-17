resolvers ++= Resolver.sonatypeOssRepos("snapshots")

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.6.4")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.19.0")
addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.21.1+36-2d9cbce2-SNAPSHOT")
addSbtPlugin("com.github.reibitto" % "sbt-welcome" % "0.5.0")
addSbtPlugin("org.typelevel" % "sbt-typelevel-scalafix" % "0.7.7")
addSbtPlugin("org.typelevel" % "sbt-typelevel-settings" % "0.7.7")
addSbtPlugin("org.typelevel" % "sbt-typelevel-ci-release" % "0.7.7")
addSbtPlugin("org.typelevel" % "sbt-typelevel-site" % "0.7.7")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.4")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.3.1")
libraryDependencies += "org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.1.0"

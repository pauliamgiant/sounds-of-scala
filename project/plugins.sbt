resolvers ++= Resolver.sonatypeOssRepos("snapshots")

addSbtPlugin("ch.epfl.scala" % "sbt-version-policy" % "3.2.1")
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.6.3")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.16.0")
addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.21.1+36-2d9cbce2-SNAPSHOT")
addSbtPlugin("com.github.reibitto" % "sbt-welcome" % "0.4.0")
addSbtPlugin("org.typelevel" % "sbt-typelevel-scalafix" % "0.7.2-12-17ac909-SNAPSHOT")
addSbtPlugin("org.typelevel" % "sbt-typelevel-settings" % "0.7.2-12-17ac909-SNAPSHOT")
addSbtPlugin("org.typelevel" % "sbt-typelevel-ci-release" % "0.7.2-12-17ac909-SNAPSHOT")
addSbtPlugin("org.typelevel" % "sbt-typelevel-site" % "0.7.2-12-17ac909-SNAPSHOT")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.2")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.3.1")
libraryDependencies += "org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.1.0"

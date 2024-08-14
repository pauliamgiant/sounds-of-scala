import laika.sbt.LaikaPlugin.autoImport.laikaTheme
import laika.helium.Helium
import laika.theme.config.Color
import laika.helium.config.HeliumIcon
import laika.helium.config.IconLink
import sbt.ThisBuild

import scala.collection.Seq

Global / onChangedBuildSource := ReloadOnSourceChanges

inThisBuild(
  List(
    tlBaseVersion := "0.1",
    startYear := Some(2024),
    licenses := Seq(License.Apache2),
    organization := "org.soundsofscala",
    organizationName := "Sounds of Scala",
    scalaVersion := "3.4.2",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    tlSonatypeUseLegacyHost := false,
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeRepository := "https://s01.oss.sonatype.org/service/local",
    tlSitePublishBranch := Some("main"),
    developers := List(
      tlGitHubDev("pauliamgiant", "Paul Matthews"),
      tlGitHubDev("noelwelsh", "Noel Welsh"),
      tlGitHubDev("ikukojohanna", "Johanna Odersky"),
      tlGitHubDev("BokChoyWarrior", "Harvey Cambridge")
    ),
    scalafixDependencies ++= List(
      "com.github.xuwei-k" %% "scalafix-rules" % "0.3.0"
    ),
    resolvers +=
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  ))

commands += Command.command("build") { state =>
  "dependencyUpdates" ::
    "compile" ::
    "test" ::
    "scalafixAll" ::
    "scalafmtAll" ::
    "scalafmtSbt" ::
    "headerCreateAll" ::
    "githubWorkflowGenerate" ::
    "docs / tlSite" ::
    "dependencyUpdates" ::
    "reload plugins; dependencyUpdates; reload return" ::
    state
}

lazy val root = project
  .in(file("."))
  .aggregate(sos.js, sos.jvm)
  .settings(
    name := "sounds-of-scala",
    publish := {}
  )

lazy val sos = crossProject(JSPlatform, JVMPlatform)
  .in(file("."))
  .settings(
    moduleName := "sounds-of-scala",
    libraryDependencies ++= Seq(
      "org.scalactic" %%% "scalactic" % "3.2.17",
      "org.scalatest" %%% "scalatest" % "3.2.17" % Test,
      "org.typelevel" %%% "cats-core" % "2.10.0",
      "org.typelevel" %%% "cats-effect" % "3.5.2",
      "io.kevinlee" %%% "refined4s-core" % "0.11.0",
      "io.kevinlee" %%% "refined4s-cats" % "0.11.0",
      "io.kevinlee" %%% "refined4s-pureconfig" % "0.11.0"
    )
  )
  .jvmSettings(
    libraryDependencies += "org.scala-js" %% "scalajs-stubs" % "1.1.0" % "provided"
  )
  .jsSettings(
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
    Compile / mainClass := Some("org.soundsofscala.Main"),
    Test / requireJsDomEnv := true,
    jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv(),
    libraryDependencies ++= Seq("org.scala-js" %%% "scalajs-dom" % "2.8.0"),
    npmExtraArgs ++= Seq(
      "--registry=https://registry.npmjs.org/"
    )
  )
  .jsConfigure(project => project.enablePlugins(ScalaJSBundlerPlugin))

lazy val docs =
  project.in(file("docs")).settings(
    description := "Documentation for Sounds of Scala",
    mdocIn := file("docs/src/pages"),
    laikaTheme := Helium.defaults
      .all.themeColors(
        primary = Color.hex("007c99"),
        secondary = Color.hex("931813"),
        primaryMedium = Color.hex("a7d4de"),
        primaryLight = Color.hex("ebf6f7"),
        text = Color.hex("5f5f5f"),
        background = Color.hex("ffffff"),
        bgGradient = (Color.hex("095269"), Color.hex("007c99"))
      ).site
      .topNavigationBar(
        homeLink = IconLink.internal(laika.ast.Path(List("README.md")), HeliumIcon.home),
        navLinks = Seq(IconLink.external(
          "https://github.com/pauliamgiant/sounds-of-scala",
          HeliumIcon.github))
      ).build,
    laikaExtensions ++= Seq(
      laika.format.Markdown.GitHubFlavor,
      laika.config.SyntaxHighlighting
    )
  ).enablePlugins(TypelevelSitePlugin)

import sbtwelcome.*
// Generated from http://patorjk.com/software/taag/#p=display&f=Stronger%20Than%20All&t=Sounds%20of%20Scala
logo :=
  raw"""
       |.________._______  .____     .______  .______  .________     ._______  ._______     .________._______ .______  .___    .______
       ||    ___/: .___  \ |    |___ :      \ :_ _   \ |    ___/     : .___  \ :_ ____/     |    ___/:_.  ___\:      \ |   |   :      \
       ||___    \| :   |  ||    |   ||       ||   |   ||___    \     | :   |  ||   _/       |___    \|  : |/\ |   .   ||   |   |   .   |
       ||       /|     :  ||    :   ||   |   || . |   ||       /     |     :  ||   |        |       /|    /  \|   :   ||   |/\ |   :   |
       ||__:___/  \_. ___/ |        ||___|   ||. ____/ |__:___/       \_. ___/ |_. |        |__:___/ |. _____/|___|   ||   /  \|___|   |
       |   :        :/     |. _____/     |___| :/         :             :/       :/            :      :/          |___||______/    |___|
       |            :       :/                 :                        :        :                    :
       |                    :
       |${scala.Console.YELLOW}Scala ${scalaVersion.value}${scala.Console.RESET}
       |
       |""".stripMargin

usefulTasks := Seq(
  UsefulTask("~fastOptJS / webpack", "Run fastOptJS for live updates").alias("f"),
  UsefulTask("reload", "run reload").alias("r"),
  UsefulTask("publishLocal", "Publish build locally").alias("pub"),
  UsefulTask("docs/tlSitePreview", "preview documentation").alias("doc"),
  UsefulTask("clean", "run clean").alias("cln"),
  UsefulTask("compile", "run compile").alias("c"),
  UsefulTask("test", "Run test").alias("t"),
  UsefulTask("scalafmtAll", "Run scalafmtAll on the entire project").alias("fmt"),
  UsefulTask(
    "scalafmtCheckAll",
    "Run scalafmtCheckAll on the entire project"
  ).alias("fmtchk"),
  UsefulTask("scalafixAll", "Run scalafixAll on the entire project").alias("fix"),
  UsefulTask(
    "scalafixAll --check",
    "Run scalafixAll --check on the entire project"
  ).alias("fixchk"),
  UsefulTask("fmtchk; fixchk", "Run fmtchk; fixchk").alias("chk"),
  UsefulTask(
    "c; t; chk",
    "Build - Run compile; test; scalafmtCheckAll; scalafixAll --check"
  ).alias("bld"),
  UsefulTask(
    "cln; c; t; chk",
    "Clean Build - Run compile; test; scalafmtCheckAll; scalafixAll --check"
  ).alias("cbld"),
  UsefulTask("dependencyUpdates", "Run dependencyUpdates").alias("du"),
  UsefulTask("headerCreate", "Run create headers for pagers to pass ci").alias("h")
)

logoColor := scala.Console.YELLOW
aliasColor := scala.Console.BLUE
commandColor := scala.Console.CYAN
descriptionColor := scala.Console.WHITE

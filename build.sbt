ThisBuild / organization := "com.soundsofscala"

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

ThisBuild / resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

ThisBuild / scalafixDependencies ++= List(
  "com.github.xuwei-k" %% "scalafix-rules" % "0.3.0"
)

ThisBuild / scalafixScalaBinaryVersion := "2.13"
scalaJSUseMainModuleInitializer := true
Compile / mainClass := Some("com.soundsofscala.Main")

enablePlugins(ScalaJSPlugin)
enablePlugins(ScalaJSBundlerPlugin)
npmExtraArgs ++= Seq(
  "--registry=https://registry.npmjs.org/"
)
Test / requireJsDomEnv := true
jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()

val monixNewtypes = "0.2.3"

scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) }
import sbtwelcome.*
logo :=
  s"""
     |
     |.________._______  .____     .______  .______  .________     ._______  ._______     .________._______ .______  .___    .______  
     ||    ___/: .___  \\ |    |___ :      \\ :_ _   \\ |    ___/     : .___  \\ :_ ____/     |    ___/:_.  ___\\:      \\ |   |   :      \\ 
     ||___    \\| :   |  ||    |   ||       ||   |   ||___    \\     | :   |  ||   _/       |___    \\|  : |/\\ |   .   ||   |   |   .   |
     ||       /|     :  ||    :   ||   |   || . |   ||       /     |     :  ||   |        |       /|    /  \\|   :   ||   |/\\ |   :   |
     ||__:___/  \\_. ___/ |        ||___|   ||. ____/ |__:___/       \\_. ___/ |_. |        |__:___/ |. _____/|___|   ||   /  \\|___|   |
     |   :        :/     |. _____/     |___| :/         :             :/       :/            :      :/          |___||______/    |___|
     |            :       :/                 :                        :        :                    :                                 
     |                    :
     |${scala.Console.YELLOW}Scala ${scalaVersion.value}${scala.Console.RESET}
     |
     |""".stripMargin

usefulTasks := Seq(
  UsefulTask("~fastOptJS / webpack", "Run fastOptJS for live updates").alias("f"),
  UsefulTask("reload", "run reload").alias("r"),
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
    "tc; t; chk",
    "Build - Run Test/compile; test; scalafmtCheckAll; scalafixAll --check"
  ).alias("bld"),
  UsefulTask(
    "cln; tc; t; chk",
    "Clean Build - Run Test/compile; test; scalafmtCheckAll; scalafixAll --check"
  ).alias("cbld"),
  UsefulTask("dependencyUpdates", "Run dependencyUpdates").alias("du")
)

logoColor := scala.Console.YELLOW
aliasColor := scala.Console.BLUE
commandColor := scala.Console.CYAN
descriptionColor := scala.Console.WHITE

lazy val root = (project in file(".")).settings(
  name := "sounds-of-scala",
  libraryDependencies ++= Seq(
    "org.scalactic" %%% "scalactic" % "3.2.17",
    "org.scalatest" %%% "scalatest" % "3.2.17" % Test,
    "org.typelevel" %%% "cats-core" % "2.10.0",
    "org.typelevel" %%% "cats-effect" % "3.5.2",
    "org.scalameta" %%% "munit" % "0.7.29" % Test,
    "org.typelevel" %%% "munit-cats-effect-3" % "1.0.7" % Test,
    "org.scala-js" %%% "scalajs-dom" % "2.8.0",
    "io.kevinlee" %%% "refined4s-core" % "0.11.0",
    "io.kevinlee" %%% "refined4s-cats" % "0.11.0",
    "io.kevinlee" %%% "refined4s-pureconfig" % "0.11.0"
  )
)

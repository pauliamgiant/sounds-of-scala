ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

ThisBuild / resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

ThisBuild / scalafixDependencies ++= List(
  "com.github.xuwei-k" %% "scalafix-rules" % "0.3.0"
)

ThisBuild / scalafixScalaBinaryVersion := "2.13"
scalaJSUseMainModuleInitializer := true

enablePlugins(ScalaJSPlugin)
enablePlugins(ScalaJSBundlerPlugin)
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
  UsefulTask("f", "~fastOptJS / webpack", "Run fastOptJS for live updates"),
  UsefulTask("r", "reload", "run reload"),
  UsefulTask("cln", "clean", "run clean"),
  UsefulTask("c", "compile", "run compile"),
  UsefulTask("t", "test", "Run test"),
  UsefulTask("fmt", "scalafmtAll", "Run scalafmtAll on the entire project"),
  UsefulTask(
    "fmtchk",
    "scalafmtCheckAll",
    "Run scalafmtCheckAll on the entire project"
  ),
  UsefulTask("fix", "scalafixAll", "Run scalafixAll on the entire project"),
  UsefulTask(
    "fixchk",
    "scalafixAll --check",
    "Run scalafixAll --check on the entire project"
  ),
  UsefulTask("chk", "fmtchk; fixchk", "Run fmtchk; fixchk"),
  UsefulTask(
    "bld",
    "tc; t; chk",
    "Build - Run Test/compile; test; scalafmtCheckAll; scalafixAll --check"
  ),
  UsefulTask(
    "cbld",
    "cln; tc; t; chk",
    "Clean Build - Run Test/compile; test; scalafmtCheckAll; scalafixAll --check"
  ),
  UsefulTask("du", "dependencyUpdates", "Run dependencyUpdates")
)

logoColor := scala.Console.YELLOW
aliasColor := scala.Console.BLUE
commandColor := scala.Console.CYAN
descriptionColor := scala.Console.WHITE

lazy val root = (project in file("."))
  .settings(
    name := "sounds-of-scala",
    libraryDependencies ++= Seq(
      "io.github.iltotore" %%% "iron" % "2.3.0",
      "org.scalactic" %%% "scalactic" % "3.2.17",
      "org.scalatest" %% "scalatest" % "3.2.17" % Test,
      "org.typelevel" %%% "cats-core" % "2.10.0",
      "org.typelevel" %%% "cats-effect" % "3.5.2",
      "org.scalameta" %%% "munit" % "0.7.29" % Test,
      "org.typelevel" %%% "munit-cats-effect-3" % "1.0.7" % Test,
      "org.scala-js" %%% "scalajs-dom" % "2.8.0"
    )
  )

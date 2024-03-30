package org.soundsofscala

import cats.effect.{ExitCode, IO, IOApp}
import org.soundsofscala.syntax.all.*

object Main extends IOApp:

  def run(args: List[String]): IO[cats.effect.ExitCode] =
    val quickTestSong = C3 + D4 + E2.+(F2)

    IO.println(quickTestSong) >> IO.unit.as(ExitCode.Success)

package org.soundsofscala

import cats.effect.{ExitCode, IO, IOApp}
import org.soundsofscala.syntax.all.*
import org.soundsofscala.models.{AtomicMusicalEvent, MusicalEvent}

object Main extends IOApp:

  def run(args: List[String]): IO[ExitCode] =
    val quickTestSong: MusicalEvent = C3 + D4 + E2.+(F2)
    IO.println("Song" + quickTestSong.toString) >> IO.unit.as(ExitCode.Success)

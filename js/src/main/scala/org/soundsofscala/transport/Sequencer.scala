/*
 * Copyright 2024 Sounds of Scala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.soundsofscala.transport

import cats.effect.IO
import cats.effect.Ref
import cats.effect.kernel.Fiber
import cats.syntax.all.*
import org.scalajs.dom.AudioContext
import org.soundsofscala.models.LookAhead
import org.soundsofscala.models.ScheduleWindow
import org.soundsofscala.models.Song
import org.soundsofscala.models.TrackIndex

/**
 * The sequencer is responsible for scheduling the notes of every track in the song in parallel. It
 * holds a `Ref[IO, Song]` so that all song properties (tempo, swing, track patterns, instruments,
 * etc.) can be updated in real time via the same Ref.
 *
 * To change the song being played, update the Song inside the Ref — don't create a new Sequencer.
 *
 * @param songRef
 *   A Ref holding the current Song, enabling live updates to any property
 */

class Sequencer private (
    val songRef: Ref[IO, Song],
    private val fiberRef: Ref[IO, Option[Fiber[IO, Throwable, Unit]]]
)(using audioContext: AudioContext):

  def updateSong(f: Song => Song): IO[Unit] = songRef.update(f)

  def play(): IO[Unit] =
    for
      song <- songRef.get
      _ <- IO.println(s"Playing: ${song.title}")
      _ <- stop()
      fiber <- startPlayback().start
      _ <- fiberRef.set(fiber.some)
    yield ()

  def stop(): IO[Unit] =
    fiberRef.getAndSet(none).flatMap:
      case Some(fiber) =>
        IO.println("Stopping sequencer") *>
          fiber.cancel *>
          stopInstruments()
      case none => IO.unit

  private def stopInstruments(): IO[Unit] =
    songRef.get.flatMap(_.mixer.tracks.parTraverse(_.instrument.stop.void).void)

  private def startPlayback(): IO[Unit] =
    songRef.get.flatMap: song =>
      val noteScheduler = NoteScheduler(songRef, LookAhead(25), ScheduleWindow(0.1))
      /* create indexes for each track so the scheduler can re-read the track from the
      songRef on every note — this enables live updates */
      song.mixer.tracks.zipWithIndex.parTraverse: (_, index) =>
        noteScheduler.scheduleTrack(TrackIndex(index))
      .void *>
        songRef.get.flatMap(s => IO.println(s"Finished playing: ${s.title}"))
end Sequencer

object Sequencer:
  def apply(songRef: Ref[IO, Song])(using AudioContext): IO[Sequencer] =
    Ref.of[IO, Option[Fiber[IO, Throwable, Unit]]](none).map: fiberRef =>
      new Sequencer(songRef, fiberRef)

end Sequencer

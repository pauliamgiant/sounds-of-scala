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
import org.soundsofscala.instrument.ClickTrack
import org.soundsofscala.models.*

/**
 * The sequencer is responsible for scheduling the notes of every track in the song in parallel. It
 * holds a `Ref[IO, Song]` so that all song properties (tempo, swing, track patterns, instruments,
 * etc.) can be updated in real time via the same Ref.
 *
 * To change the song being played, update the Song inside the Ref — don't create a new Sequencer.
 *
 * @param songRef
 *   A Ref holding the current Song, enabling live updates to any property
 * @param beatPositionRef
 *   A Ref containing the current beat position while playing
 * @param pausedAtRef
 *   A Ref holding beat position of song when paused
 */

class Sequencer private (
    val songRef: Ref[IO, Song],
    private val fiberRef: Ref[IO, Option[Fiber[IO, Throwable, Unit]]],
    private val beatPositionRef: Ref[IO, BeatPosition],
    private val pausedAtRef: Ref[IO, Option[BeatPosition]],
    val clickTrack: ClickTrack
)(using audioContext: AudioContext):

  def updateSong(f: Song => Song): IO[Unit] = songRef.update(f)

  def currentBeatPosition: IO[BeatPosition] = beatPositionRef.get

  def toggleClick: IO[Boolean] = clickTrack.toggleMute

  def play(): IO[Unit] =
    for
      song <- songRef.get
      pausedAt <- pausedAtRef.get
      _ <- IO.println(pausedAt.fold(s"Playing: ${song.title}")(bp =>
        s"Resuming: ${song.title} from beat ${bp.value}"))
      _ <- stop()
      fiber <- startPlaybackFromBeat(pausedAt).start
      _ <- fiberRef.set(fiber.some)
    yield ()

  def stop(): IO[Unit] =
    fiberRef.getAndSet(none).flatMap:
      case Some(fiber) =>
        IO.println("Stopping sequencer") *>
          fiber.cancel *>
          stopInstruments() *>
          beatPositionRef.set(BeatPosition(0.0)) *>
          pausedAtRef.set(none)
      case none => IO.unit

  def pause(): IO[Unit] =
    for
      _ <- fiberRef.getAndSet(none).flatMap {
        case Some(fiber) =>
          fiber.cancel *>
            stopInstruments()
        case none => IO.unit
      }
      beatPosition <- beatPositionRef.get
      _ <- pausedAtRef.set(beatPosition.some)
      _ <- IO.println(s"Paused at beat: ${beatPosition.value}")
    yield ()

  private def stopInstruments(): IO[Unit] =
    clickTrack.stop.void *>
      songRef.get.flatMap(_.mixer.tracks.parTraverse(_.instrument.stop.void).void)

  private def startPlaybackFromBeat(positionToStartAt: Option[BeatPosition]): IO[Unit] =
    positionToStartAt.fold(IO.unit)(beatPositionRef.set) >>
      songRef.get.flatMap: song =>
        val noteScheduler =
          NoteScheduler(
            songRef,
            beatPositionRef,
            LookAhead(25),
            ScheduleWindow(0.1),
            ClickTrack.track(clickTrack))

        val scheduleClick = noteScheduler.scheduleTrack(TrackIndex(0), positionToStartAt)
        val scheduleSongTracks = (1 to song.mixer.tracks.size).toList.parTraverse: index =>
          noteScheduler.scheduleTrack(TrackIndex(index), positionToStartAt)

        // We use IO.race to stop the click track when the song finishes
        IO.race(scheduleSongTracks.void, scheduleClick).void *>
          songRef.get.flatMap(song => IO.println(s"Finished playing: ${song.title}"))
end Sequencer

object Sequencer:
  def apply(songRef: Ref[IO, Song])(using AudioContext): IO[Sequencer] =
    for
      fiberRef <- Ref.of[IO, Option[Fiber[IO, Throwable, Unit]]](none)
      beatPositionRef <- Ref.of[IO, BeatPosition](BeatPosition(0.0))
      pausedAtRef <- Ref.of[IO, Option[BeatPosition]](None)
      clickTrack <- ClickTrack()
    yield new Sequencer(songRef, fiberRef, beatPositionRef, pausedAtRef, clickTrack)

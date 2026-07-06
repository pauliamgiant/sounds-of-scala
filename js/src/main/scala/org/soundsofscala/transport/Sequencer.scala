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
 * The Sequencer class is the key piece of Kit in the Sounds of Scala library for playing musical
 * events.
 *
 * It orchestrates parallel playback of all tracks in a song. The Song is held in a `Ref[IO, Song]`
 * so that tempo, swing, patterns, and instruments can be updated in real time.
 *
 * To change the song being played, update the Song inside the Ref - don't create a new Sequencer.
 *
 * KEY FEATURES:
 *
 * ====Lookahead Scheduling====
 *
 * The sequencer uses a lookahead to schedule notes in advance. This ensures notes are played in the
 * correct order and at the correct time on a single-threaded JavaScript runtime. The lookahead is
 * set to 25ms by default and can be changed by passing a different lookahead value to the Sequencer
 * constructor.. Based on: [[https://web.dev/articles/audio-scheduling Audio Scheduling]]
 *
 * It seems unlikely you'd need to change the lookahead value but the option is there in the event
 * you encounter performance issues.
 *
 * ====Click Track====
 *
 * The click track has two purposes:
 *
 *   1. BEAT POSITION - It owns the song's beat position, advancing the beatPositionRef at 16th-note
 *      resolution (0.25 beat increments),
 *
 * This serves as the master timeline that other tracks and the UI reference for scheduling,
 * pause/resume, and display.
 *
 *   2. METRONOME - It provides an audible metronome click for the user to play along with / keep
 *      time.
 *
 * ====Live Updates====
 *
 * Live song updates so you can change the song while it is playing has been added and is enabled by
 * passing a Cats Effect Ref to the Sequencer constructor.
 *
 * FUTURE CONSIDERATION: It may be possible to encapsulate the Ref in the sequencer itself and can
 * look into this in future releases.
 *
 * ====Looping Tracks====
 *
 * We have added the capability of looping tracks to the sequencer. This is determined by the
 * Playback enum in the Track class and the TrackScheduler owns handling the looping logic.
 *
 * ====Resume Playback====
 *
 * This was a feature required by the 'Pause' functionality. It means a song can be resumed from any
 * point in the song. Up until this point we had no concept of tracking the location of the song.
 * Two ways of doing this were considered:
 *
 *   1. Using the AudioContext.currentTime to track the time of the song in minutes, seconds and
 *      milliseconds.
 *   2. Using a custom timekeeping system by beats and sub beats.
 *
 * We have started with the second option and are using a custom timekeeping system by beats and sub
 * beats. This allows us to track the location of the song at any point in the song. This is done by
 * keeping a record of the beat position as defined by the click track. As stated above, the beat
 * position is updated by the click track at 16th note resolution (0.25 beat increments).
 *
 * BEAT OFFSET - this arose from the fact that different tracks have different subdivision patterns
 * at the point of resuming playback. When first playing back a song.. different tracks played back
 * at different times so we needed to calculate the offset to ensure all tracks played back at the
 * same time.
 *
 * The TrackScheduler is responsible for calculating the beat offset and passing it to the
 * NoteScheduler.
 *
 * ====Swing Calculation====
 *
 * We recently added the capability of adding swing to a sequence of notes at different strengths.
 * This is done by calculating the swing offset for each note in the sequence. It ultimatelymeans
 * adding some time to a note, and then subtracting some time from the next note.
 */

trait Sequencer:
  /**
   * Updates the Song in real time.
   */
  def updateSong(updateFunction: Song => Song): IO[Unit]

  /**
   * Returns the current beat position of the song while the song is playing.
   */
  def currentBeatPosition: IO[BeatPosition]

  def toggleClickOnOff: IO[Boolean]

  def play(): IO[Unit]

  /**
   * Does indeed stop playback but notably resets the position to the start.
   */
  def stop(): IO[Unit]

  /**
   * Pauses playback, preserving the current beat position for resume.
   */
  def pause(): IO[Unit]

  /**
   * Starts playback from a specific beat position.
   */
  def startPlaybackFromBeat(barPositionToStartAt: Option[BeatPosition]): IO[Unit]
end Sequencer

object Sequencer:
  def apply(
      songRef: Ref[IO, Song],
      lookAhead: LookAhead = LookAhead(25),
      scheduleWindow: ScheduleWindow = ScheduleWindow(0.1)
  )(using AudioContext): IO[Sequencer] =
    for
      fiberRef <- Ref.of[IO, Option[Fiber[IO, Throwable, Unit]]](none)
      beatPositionRef <- Ref.of[IO, BeatPosition](BeatPosition(0.0))
      pausedAtRef <- Ref.of[IO, Option[BeatPosition]](None)
      clickTrack <- ClickTrack()
    yield new SequencerImpl(
      songRef,
      fiberRef,
      beatPositionRef,
      pausedAtRef,
      clickTrack,
      lookAhead,
      scheduleWindow)

  private class SequencerImpl(
      songRef: Ref[IO, Song],
      fiberRef: Ref[IO, Option[Fiber[IO, Throwable, Unit]]],
      beatPositionRef: Ref[IO, BeatPosition],
      pausedAtRef: Ref[IO, Option[BeatPosition]],
      clickTrack: ClickTrack,
      lookAhead: LookAhead,
      scheduleWindow: ScheduleWindow
  )(using audioContext: AudioContext)
      extends Sequencer:

    def updateSong(updateFunction: Song => Song): IO[Unit] = songRef.update(updateFunction)

    def currentBeatPosition: IO[BeatPosition] = beatPositionRef.get

    def toggleClickOnOff: IO[Boolean] = clickTrack.toggleMute

    def play(): IO[Unit] =
      for
        song <- songRef.get
        pausedAt <- pausedAtRef.get
        _ <- IO.println(pausedAt.fold(s"Playing: ${song.title}")(beatPosition =>
          s"Resuming: ${song.title} from beat ${beatPosition.value}"))
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

    def startPlaybackFromBeat(barPositionToStartAt: Option[BeatPosition]): IO[Unit] =
      barPositionToStartAt.fold(IO.unit)(beatPositionRef.set) >>
        songRef.get.flatMap: song =>
          val trackScheduler =
            TrackScheduler(
              songRef,
              beatPositionRef,
              lookAhead,
              scheduleWindow,
              ClickTrack.track(clickTrack))

          val scheduleClick = trackScheduler.scheduleTrack(TrackIndex(0), barPositionToStartAt)

          val scheduleSongTracks = (1 to song.mixer.tracks.size).toList.parTraverse: index =>
            trackScheduler.scheduleTrack(TrackIndex(index), barPositionToStartAt)

          /**
           * Using race here essentially to stop the click track from playing once the song track
           * ends. The clicks musical event loops infinitely and needs to be conditional on the
           * song..
           */
          IO.race(scheduleSongTracks.void, scheduleClick).void *>
            songRef.get.flatMap(song => IO.println(s"Finished playing: ${song.title}"))
  end SequencerImpl
end Sequencer

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
import org.scalajs.dom.AudioContext
import org.soundsofscala.models.*

import scala.concurrent.duration.DurationDouble

/**
 * The `NoteScheduler` is responsible for scheduling the notes of a single track. It reads the
 * current Song from a `Ref[IO, Song]`, extracting the track by index. Tempo is re-read per-note for
 * live tempo changes. The track's pattern, instrument, and playback mode are re-read at each cycle
 * boundary.
 *
 * @param songRef
 *   A Ref holding the current Song, re-read for live updates
 * @param lookAheadMs
 *   The time in milliseconds to look ahead to determine if the next note should be scheduled
 * @param scheduleAheadTimeSeconds
 *   The time window in seconds in which to schedule notes ahead of time
 */
final case class NoteScheduler(
    songRef: Ref[IO, Song],
    lookAheadMs: LookAhead,
    scheduleAheadTimeSeconds: ScheduleWindow):

  def scheduleTrack(trackIndex: Int)(using ac: AudioContext): IO[Unit] =
    def loop(nextNoteTime: NextNoteTime): IO[Unit] =
      for
        song <- songRef.get
        track = song.mixer.tracks.toList(trackIndex)
        finalNoteTime <- scheduleSequence(
          track,
          track.musicalEvent,
          nextNoteTime)
        _ <-
          if track.playback == Playback.Loop
          then loop(finalNoteTime)
          else IO.unit
      yield ()

    loop(NextNoteTime(ac.currentTime))

  private def scheduleSequence(
      track: Track[?],
      musicalEvent: MusicalEvent,
      nextNoteTime: NextNoteTime)(using AudioContext): IO[NextNoteTime] =
    ScheduleStatus(nextNoteTime, scheduleAheadTimeSeconds) match
      case ScheduleStatus.Ready =>
        songRef.get.flatMap: song =>
          val tempo = song.tempo
          musicalEvent match
            case sequence: Sequence =>
              val nextNextNoteTime =
                NextNoteTime(nextNoteTime.value + sequence.head.durationToSeconds(tempo))
              track.playAtomicMusicalEvent(sequence.head, nextNoteTime.value, tempo) >>
                scheduleSequence(track, sequence.tail, nextNextNoteTime)
            case atomicEvent: AtomicMusicalEvent =>
              val nextNextNoteTime =
                NextNoteTime(nextNoteTime.value + atomicEvent.durationToSeconds(tempo))
              track.playAtomicMusicalEvent(atomicEvent, nextNoteTime.value, tempo)
                .as(nextNextNoteTime)

      case ScheduleStatus.Waiting =>
        IO.sleep(lookAheadMs.value.millis) >>
          scheduleSequence(track, musicalEvent, nextNoteTime)
end NoteScheduler

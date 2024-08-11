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
import org.scalajs.dom
import org.scalajs.dom.AudioContext
import org.soundsofscala.instrument.Instrument
import org.soundsofscala.models
import org.soundsofscala.models.AtomicMusicalEvent.*
import org.soundsofscala.models.*

import scala.concurrent.duration.DurationDouble

/**
 * The `NoteScheduler` is responsible for scheduling the notes of a single track. It uses the
 * `ScheduleStatus` to determine if the next note should be scheduled. We can start them at a
 * precise time with the currentTime property of the AudioContext. The method of scheduling notes in
 * the browser is derived from the following: https://web.dev/articles/audio-scheduling
 * https://developer.mozilla.org/en-US/docs/Web/API/Web_Audio_API/Advanced_techniques#playing_the_audio_in_time
 *
 * @param tempo
 *   The tempo of the song. This is needed to determine the how many seconds a note is played for
 * @param lookAheadMs
 *   The time in milliseconds to look ahead to determine if the next note should be scheduled
 * @param scheduleAheadTimeSeconds
 *   The time window in seconds in which to schedule notes ahead of time
 */
final case class NoteScheduler(
    tempo: Tempo,
    lookAheadMs: LookAhead,
    scheduleAheadTimeSeconds: ScheduleWindow):

  def scheduleInstrument[Settings](
      musicalEvent: MusicalEvent,
      instrument: Instrument[Settings],
      settings: Settings)(
      using audioContext: AudioContext): IO[Unit] =

    val startingNoteTime = NextNoteTime(audioContext.currentTime)
    scheduler(musicalEvent, startingNoteTime, instrument, settings) >> IO.println(
      "Sequence finished")

  private def scheduler[Settings](
      musicalEvent: MusicalEvent,
      nextNoteTime: NextNoteTime,
      instrument: Instrument[Settings],
      settings: Settings): AudioContext ?=> IO[Unit] =
    // check if we can schedule the next note
    ScheduleStatus(nextNoteTime, scheduleAheadTimeSeconds) match
      case ScheduleStatus.Ready =>
        musicalEvent match
          // for a sequence, schedule the first note, and then schedule the rest of the sequence
          case sequence: Sequence =>
            // time that rest of the sequence will be scheduled
            val nextNextNoteTime =
              NextNoteTime(nextNoteTime.value + sequence.head.durationToSeconds(tempo))
            // schedule the first note
            scheduleAtomicEvent(sequence.head, nextNoteTime, instrument, settings) >>
              // schedule the rest of the sequence
              scheduler(sequence.tail, nextNextNoteTime, instrument, settings)
          case atomicEvent: AtomicMusicalEvent =>
            scheduleAtomicEvent(atomicEvent, nextNoteTime, instrument, settings)

      case ScheduleStatus.Waiting =>
        // no note to schedule, wait for specified time and look ahead again
        IO.sleep(lookAheadMs.value.millis)
          >> scheduler(
            musicalEvent,
            nextNoteTime,
            instrument,
            settings)

  private def scheduleAtomicEvent[Settings](
      musicalEvent: AtomicMusicalEvent,
      nextNoteTime: NextNoteTime,
      instrument: Instrument[Settings],
      settings: Settings): AudioContext ?=> IO[Unit] =
    musicalEvent match
      case Rest(_) => IO.unit
      case event: AtomicMusicalEvent =>
        instrument.play(event, nextNoteTime.value, tempo)(settings)
end NoteScheduler

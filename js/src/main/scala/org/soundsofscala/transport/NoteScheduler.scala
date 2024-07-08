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

case class NoteScheduler(
    tempo: Tempo,
    lookAheadMs: LookAhead,
    scheduleAheadTimeSeconds: ScheduleWindow):

  enum ScheduleState:
    case Ready extends ScheduleState
    case Waiting extends ScheduleState

  object ScheduleState:
    def apply(nextNoteTime: NextNoteTime)(using audioContext: AudioContext): ScheduleState =
      if nextNoteTime.value < audioContext.currentTime + scheduleAheadTimeSeconds.value then
        Ready
      else Waiting

  def scheduleInstrument[Settings](
      musicalEvent: MusicalEvent,
      instrument: Instrument[Settings],
      settings: Settings)(
      using audioContext: AudioContext): IO[Unit] =
    val initialNextNoteValue = NextNoteTime(audioContext.currentTime)
    scheduler(musicalEvent, initialNextNoteValue, instrument, settings) >> IO.println(
      "Sequence finished")

  private def scheduler[Settings](
      musicalEvent: MusicalEvent,
      nextNoteTime: NextNoteTime,
      instrument: Instrument[Settings],
      settings: Settings): AudioContext ?=> IO[Unit] =
    ScheduleState(nextNoteTime) match
      case ScheduleState.Ready =>
        musicalEvent match
          case sequence: Sequence =>
            val time = NextNoteTime(nextNoteTime.value + sequence.head.durationToSeconds(tempo))
            scheduleAtomicEvent(sequence.head, nextNoteTime, instrument, settings) >>
              scheduler(sequence.tail, time, instrument, settings)
          case atomicEvent: AtomicMusicalEvent =>
            scheduleAtomicEvent(atomicEvent, nextNoteTime, instrument, settings)
      case ScheduleState.Waiting =>
        IO.sleep(lookAheadMs.value.millis) >> scheduler(
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

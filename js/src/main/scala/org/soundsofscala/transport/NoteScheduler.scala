package org.soundsofscala.transport

import cats.effect.IO
import org.scalajs.dom
import org.scalajs.dom.AudioContext
import org.soundsofscala.Instruments.Instrument
import org.soundsofscala.models
import org.soundsofscala.models.*
import org.soundsofscala.models.AtomicMusicalEvent.*

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

  def scheduleInstrument(musicalEvent: MusicalEvent, instrument: Instrument)(
      using audioContext: AudioContext): IO[Unit] =
    val initialNextNoteValue = NextNoteTime(audioContext.currentTime)
    IO.println(s"Playing instrument: $instrument") >>
      scheduler(musicalEvent, initialNextNoteValue, instrument) >> IO.println(
        "Sequence finished")

  private def scheduler(
      musicalEvent: MusicalEvent,
      nextNoteTime: NextNoteTime,
      instrument: Instrument): AudioContext ?=> IO[Unit] =
    ScheduleState(nextNoteTime) match
      case ScheduleState.Ready =>
        musicalEvent match
          case sequence: Sequence =>
            val time = NextNoteTime(nextNoteTime.value + sequence.head.durationToSeconds(tempo))
            scheduleAtomicEvent(sequence.head, nextNoteTime, instrument) >>
              scheduler(sequence.tail, time, instrument)
          case atomicEvent: AtomicMusicalEvent =>
            scheduleAtomicEvent(atomicEvent, nextNoteTime, instrument)
      case ScheduleState.Waiting =>
        IO.sleep(lookAheadMs.value.millis) >> scheduler(musicalEvent, nextNoteTime, instrument)

  private def scheduleAtomicEvent(
      musicalEvent: AtomicMusicalEvent,
      nextNoteTime: NextNoteTime,
      instrument: Instrument): AudioContext ?=> IO[Unit] =
    musicalEvent match
      case note: Note =>
        instrument.play(note, nextNoteTime.value, Attack(0), Release(0.9), tempo)
      case drumStroke: DrumStroke =>
        instrument.play(drumStroke, nextNoteTime.value, Attack(0), Release(0.9), tempo)
      case Rest(_) => IO.unit
      // TODO implement Chords
      case AtomicMusicalEvent.Harmony(_, _) => IO.unit

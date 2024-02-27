package com.soundsofscala.transport

import cats.effect.IO
import com.soundsofscala.Instruments.Instrument
import com.soundsofscala.models
import com.soundsofscala.models.*
import com.soundsofscala.models.AtomicMusicalEvent.*
import org.scalajs.dom
import org.scalajs.dom.AudioContext

import scala.concurrent.duration.DurationDouble
import cats.effect.unsafe.implicits.global

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

  def scheduler(musicalEvent: MusicalEvent, nextNoteTime: NextNoteTime, instrument: Instrument)(
      using audioContext: AudioContext): IO[Unit] =
    musicalEvent match
      case Sequence(head, tail) =>
        ScheduleState(nextNoteTime) match
          case ScheduleState.Ready =>
            for
              _ <- scheduler(head, nextNoteTime, instrument)
              _ <- scheduler(
                tail,
                NextNoteTime(nextNoteTime.value + head.durationToSeconds(tempo)),
                instrument)
            yield IO.unit
          case ScheduleState.Waiting =>
            IO.sleep(lookAheadMs.value.millis) >> scheduler(
              musicalEvent,
              nextNoteTime,
              instrument)

      case event: AtomicMusicalEvent =>
        ScheduleState(nextNoteTime) match
          case ScheduleState.Ready =>
            event match
              case note: AtomicMusicalEvent.Note =>
                for {
                  _ <- instrument.play(note, nextNoteTime.value, 0, Release(0.9), tempo)
                } yield IO.unit
              case drumStroke: AtomicMusicalEvent.DrumStroke =>
                for {
                  _ <- instrument.play(drumStroke, nextNoteTime.value, 0, Release(0.9), tempo)
                } yield IO.unit
              case AtomicMusicalEvent.Rest(_) =>
                IO.unit
              // TODO implement Chords
              case AtomicMusicalEvent.Harmony(_, _) =>
                IO.unit
          case ScheduleState.Waiting =>
            IO.sleep(lookAheadMs.value.millis) >> scheduler(
              musicalEvent,
              nextNoteTime,
              instrument)

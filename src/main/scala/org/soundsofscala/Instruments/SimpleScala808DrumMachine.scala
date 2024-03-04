package org.soundsofscala.Instruments

import cats.effect.IO
import cats.syntax.all.*
import org.soundsofscala.models
import org.soundsofscala.models.*
import org.soundsofscala.models.AtomicMusicalEvent.*
import org.scalajs.dom
import org.scalajs.dom.AudioContext
import org.soundsofscala.models.{LookAhead, MusicalEvent, ScheduleWindow, Tempo}
import org.soundsofscala.transport.NoteScheduler

case class SimpleScala808DrumMachine(
    tempo: Tempo,
    lookAheadMs: LookAhead,
    scheduleAheadTimeSeconds: ScheduleWindow):
  def playGroove(
      drumMusicalEvents: MusicalEvent*
  )(using audioContext: AudioContext): IO[Unit] =
    drumMusicalEvents.parTraverse { drumMusicalEvent =>
      NoteScheduler(tempo, lookAheadMs, scheduleAheadTimeSeconds).scheduleInstrument(
        drumMusicalEvent,
        SimpleDrums())
    }.void

package com.soundsofscala.Instruments

import cats.effect.IO
import cats.syntax.all.*
import com.soundsofscala.models
import com.soundsofscala.models.*
import com.soundsofscala.models.AtomicMusicalEvent.*
import com.soundsofscala.transport.NoteScheduler
import org.scalajs.dom
import org.scalajs.dom.AudioContext

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

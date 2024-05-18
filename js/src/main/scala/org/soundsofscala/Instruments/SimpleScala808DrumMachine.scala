package org.soundsofscala.Instruments

import cats.effect.IO
import cats.syntax.all.*
import org.scalajs.dom
import org.scalajs.dom.AudioContext
import org.soundsofscala.models
import org.soundsofscala.models.*
import org.soundsofscala.transport.NoteScheduler

/**
 * Simplest possible drum machine that plays a sequence of drum events
 * @param tempo
 *   takes the speed of the music in beats per minute
 */
case class SimpleScala808DrumMachine(tempo: Tempo):
  def playGroove(
      drumMusicalEvents: MusicalEvent*
  )(using audioContext: AudioContext): IO[Unit] =
    drumMusicalEvents.parTraverse { drumMusicalEvent =>
      NoteScheduler(tempo, LookAhead(25), ScheduleWindow(0.1))
        .scheduleInstrument(drumMusicalEvent, SimpleDrums())
    }.void

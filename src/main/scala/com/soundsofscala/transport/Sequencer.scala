package com.soundsofscala.transport

import cats.effect.IO
import com.soundsofscala.models.{LookAhead, ScheduleWindow, Song}
import org.scalajs.dom.AudioContext
import cats.syntax.all.*

case class Sequencer():
  def playSong(song: Song)(using audioContext: AudioContext): IO[Unit] =
    val noteScheduler = NoteScheduler(song.tempo, LookAhead(25), ScheduleWindow(0.1))
    song
      .mixer
      .tracks
      .parTraverse { track =>
        noteScheduler.scheduleInstrument(track.musicalEvent, track.instrument)
      }
      .void

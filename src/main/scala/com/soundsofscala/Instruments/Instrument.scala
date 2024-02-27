package com.soundsofscala.Instruments

import cats.effect.IO
import com.soundsofscala.models
import com.soundsofscala.models.AtomicMusicalEvent.{DrumStroke, Note}
import com.soundsofscala.models.{AtomicMusicalEvent, Release, Tempo}
import com.soundsofscala.synthesis.Oscillator.{
  SawtoothOscillator,
  SquareOscillator,
  TriangleOscillator
}
import com.soundsofscala.transport.SimpleSamplePlayer
import org.scalajs.dom
import org.scalajs.dom.AudioContext

sealed trait Instrument:
  def play(
      musicEvent: AtomicMusicalEvent,
      when: Double,
      attack: Double,
      release: Release,
      tempo: Tempo)(using audioContext: dom.AudioContext): IO[Unit] =
    this match
      case SimplePiano() =>
        musicEvent match
          case note: AtomicMusicalEvent.Note =>
            val filePath = s"resources/audio/piano/${note.pitch}${note.octave.value}.wav"
            SimpleSamplePlayer().playSample(filePath, musicEvent, when)
          case _ => IO.unit
      case scalaSynth: ScalaSynth =>
        musicEvent match
          case note: AtomicMusicalEvent.Note =>
            scalaSynth.attackRelease(when, note, tempo, release)
          case _ => IO.unit

      case SimpleDrums() =>
        musicEvent match
          case drumStroke: DrumStroke =>
            val filePath = drumStroke.drum match
              case models.DrumVoice.Kick => "resources/audio/drums/NeonKick.wav"
              case models.DrumVoice.Snare => "resources/audio/drums-808/Snare808.wav"
              case models.DrumVoice.HiHatClosed => "resources/audio/drums-808/Hats808.wav"
              case models.DrumVoice.Clap => "resources/audio/drums-808/Clap808.wav"
              case _ => "resources/audio/drums-808/G.wav"
            SimpleSamplePlayer().playSample(filePath, musicEvent, when)
          case _ => IO.unit

final case class SimplePiano() extends Instrument
final case class SimpleDrums() extends Instrument
final case class ScalaSynth()(using audioContext: AudioContext) extends Instrument {
  def attackRelease(
      when: Double,
      note: Note,
      tempo: Tempo,
      release: Release = Release(1)): IO[Unit] = {
    IO.delay {
      val keyNote = note.pitch.calculateFrequency
      val velocity = note.velocity.getNormalisedVelocity / 3
      val oscillators = Seq(
        SawtoothOscillator(keyNote / 4, velocity),
        SquareOscillator(keyNote / 4, velocity),
        TriangleOscillator(keyNote / 2 - 3, velocity)
      )
      oscillators.foreach { osc =>
        osc.play(when)
        osc.stop(when + (note.durationToSeconds(tempo) * release.value))
      }
    }
  }
}

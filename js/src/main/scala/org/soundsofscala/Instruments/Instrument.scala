package org.soundsofscala.Instruments

import cats.effect.IO
import org.soundsofscala.models
import org.soundsofscala.models.AtomicMusicalEvent.{DrumStroke, Note}
import org.soundsofscala.synthesis.Oscillator.*
import org.scalajs.dom
import org.scalajs.dom.AudioContext
import org.soundsofscala.models.*
import org.soundsofscala.models.AtomicMusicalEvent.{DrumStroke, Note}
import org.soundsofscala.models.DrumVoice.*
import org.soundsofscala.synthesis.DrumGeneration
import org.soundsofscala.transport.SimpleSamplePlayer

sealed trait Instrument:
  def play(
      musicEvent: AtomicMusicalEvent,
      when: Double,
      attack: Attack,
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
      case SimpleDrumSynth() =>
        musicEvent match
          case drumStroke: AtomicMusicalEvent.DrumStroke =>
            drumStroke.drum match
              case Kick =>
                DrumGeneration.generateKick808(drumStroke, when)
              case Snare =>
                DrumGeneration.generateSnare808(drumStroke, when)
              case HiHatClosed =>
                DrumGeneration.generateHats808(drumStroke, when)
              case Clap =>
                DrumGeneration.generateClap808(drumStroke, when)
              case _ => DrumGeneration.generateKick808(drumStroke, when)
          case _ => IO.unit
      case SimpleDrums() =>
        musicEvent match
          case drumStroke: DrumStroke =>
            val filePath = drumStroke.drum match
              case DrumVoice.Kick => "resources/audio/drums/NeonKick.wav"
              case DrumVoice.Snare => "resources/audio/drums-808/Snare808.wav"
              case DrumVoice.HiHatClosed => "resources/audio/drums-808/Hats808.wav"
              case DrumVoice.Clap => "resources/audio/drums-808/Clap808.wav"
              case _ => "resources/audio/drums-808/G.wav"
            SimpleSamplePlayer().playSample(filePath, musicEvent, when)
          case _ => IO.unit

final case class SimplePiano() extends Instrument
final case class SimpleDrums() extends Instrument
final case class SimpleDrumSynth() extends Instrument

/**
 * A simple example synthesizer that combines 3 oscillators at different octaves
 * @param audioContext
 */
final case class ScalaSynth()(using audioContext: AudioContext) extends Instrument:
  def attackRelease(when: Double, note: Note, tempo: Tempo, release: Release): IO[Unit] =
    IO:
      val keyNote = note.pitch.calculateFrequency
      val sineVelocity = note.velocity.getNormalisedVelocity
      val sawVelocity = note.velocity.getNormalisedVelocity / 10
      val triangleVelocity = note.velocity.getNormalisedVelocity / 8
      val oscillators = Seq(
        SineOscillator(Frequency(keyNote / 4), Volume(sineVelocity)),
        TriangleOscillator(Frequency(keyNote / 2), Volume(triangleVelocity)),
        SineOscillator(Frequency(keyNote / 8), Volume(sawVelocity))
      )
      oscillators.foreach: osc =>
        osc.play(when)
        osc.stop(when + note.durationToSeconds(tempo))

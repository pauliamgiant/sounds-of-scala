package org.soundsofscala.Instruments

import cats.effect.IO
import cats.syntax.parallel.catsSyntaxParallelTraverse1
import org.scalajs.dom
import org.scalajs.dom.AudioContext
import org.soundsofscala
import org.soundsofscala.models
import org.soundsofscala.models.*
import org.soundsofscala.models.AtomicMusicalEvent.{DrumStroke, Harmony, Note}
import org.soundsofscala.models.DrumVoice.*
import org.soundsofscala.synthesis.DrumGeneration
import org.soundsofscala.synthesis.Oscillator.*
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
        def playSingleNote(note: Note): IO[Unit] =
          val filePath = s"resources/audio/piano/${note.pitch}${note.octave.value}.wav"
          IO.println(s"Velocity: $note") >>
            SimpleSamplePlayer().playSample(filePath, note, when)
        musicEvent match
          case note: Note =>
            playSingleNote(note)
          case harmony: Harmony =>
            harmony
              .notes
              .toList
              .parTraverse(harmonyTiming =>
                harmonyTiming.note match
                  case singleNote: Note => playSingleNote(singleNote)
                  case _ => IO.unit
              )
              .void
          case _ => IO.unit
      case synth: Synth =>
        musicEvent match
          case note: AtomicMusicalEvent.Note =>
            synth.attackRelease(when, note, tempo, release)
          case chord: AtomicMusicalEvent.Harmony =>
            chord.notes.parTraverse(note => play(note.note, when, attack, release, tempo)).void
          case _ => IO.unit
      case SimpleDrumMachine() =>
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
end Instrument

final case class SimplePiano() extends Instrument
final case class SimpleDrums() extends Instrument
final case class SimpleDrumMachine() extends Instrument

trait Synth()(using AudioContext) extends Instrument:
  /**
   * Plays the note via an oscillator (or many)
   */
  def attackRelease(when: Double, note: Note, tempo: Tempo, release: Release): IO[Unit]
end Synth

object Synth:
  def apply()(using audioContext: AudioContext): Synth = ScalaSynth()
  def default()(using audioContext: AudioContext): Synth = Synth()
  def simpleSine()(using audioContext: AudioContext): Synth = SineSynth()
  def simpleSawtooth()(using audioContext: AudioContext): Synth = SawtoothSynth()
end Synth

/**
 * A simple example synthesizer that combines 3 oscillators at different octaves
 * @param audioContext
 */
final case class ScalaSynth()(using audioContext: AudioContext)
    extends Synth()(using audioContext: AudioContext):
  override def attackRelease(when: Double, note: Note, tempo: Tempo, release: Release): IO[Unit] =
    IO:
      val keyNote = note.frequency
      val sineVelocity = note.velocity.getNormalisedVelocity
//      val sawVelocity = note.velocity.getNormalisedVelocity / 20
      val triangleVelocity = note.velocity.getNormalisedVelocity / 4
      val oscillators = Seq(
        SineOscillator(Frequency(keyNote), Volume(sineVelocity)),
        TriangleOscillator(Frequency(keyNote * 2), Volume(triangleVelocity))
//        SawtoothOscillator(Frequency(keyNote), Volume(sawVelocity))
      )
      oscillators.foreach: osc =>
        osc.play(when)
        osc.stop(when + note.durationToSeconds(tempo))

final case class SineSynth()(using audioContext: AudioContext)
    extends Synth()(using audioContext: AudioContext):
  override def attackRelease(when: Double, note: Note, tempo: Tempo, release: Release): IO[Unit] =
    IO:
      val sineVelocity = note.velocity.getNormalisedVelocity
      val oscillators = Seq(
        SineOscillator(Frequency(note.frequency), Volume(sineVelocity))
      )
      oscillators.foreach: osc =>
        osc.play(when)
        osc.stop(when + note.durationToSeconds(tempo))

final case class SawtoothSynth()(using audioContext: AudioContext)
    extends Synth()(using audioContext: AudioContext):
  override def attackRelease(when: Double, note: Note, tempo: Tempo, release: Release): IO[Unit] =
    IO:
      val sawVelocity = note.velocity.getNormalisedVelocity / 4
      val oscillators = Seq(
        SawtoothOscillator(Frequency(note.frequency), Volume(sawVelocity))
      )
      oscillators.foreach: osc =>
        osc.play(when)
        osc.stop(when + note.durationToSeconds(tempo))

final case class PianoSynth()(using audioContext: AudioContext)
    extends Synth()(using audioContext: AudioContext):
  override def attackRelease(when: Double, note: Note, tempo: Tempo, release: Release): IO[Unit] =
    IO:
      val keyNote = note.frequency * 4
      val sineVelocity = note.velocity.getNormalisedVelocity
      //      val sawVelocity = note.velocity.getNormalisedVelocity / 10
      val triangleVelocity = note.velocity.getNormalisedVelocity / 8
      val oscillators = Seq(
        SineOscillator(Frequency(keyNote / 8), Volume(sineVelocity)),
        TriangleOscillator(Frequency(keyNote / 4), Volume(triangleVelocity))
        //        SquareOscillator(Frequency(keyNote / 8), Volume(sawVelocity))
      )
      oscillators.foreach: osc =>
        osc.play(when)
        osc.stop(when + note.durationToSeconds(tempo))

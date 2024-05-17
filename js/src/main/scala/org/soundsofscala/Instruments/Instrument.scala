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
import org.soundsofscala.synthesis.{DrumGeneration, Oscillator, WaveType}
import org.soundsofscala.synthesis.Oscillator.*
import org.soundsofscala.transport.SimpleSamplePlayer

trait Instrument:
  def play(
      musicEvent: AtomicMusicalEvent,
      when: Double,
      attack: Attack,
      release: Release,
      tempo: Tempo)(using audioContext: dom.AudioContext): IO[Unit]
end Instrument

final case class SimplePiano() extends Instrument:
  private def playSingleNote(note: Note, when: Double)(using
  audioContext: dom.AudioContext): IO[Unit] =
    val filePath = s"resources/audio/piano/${note.pitch}${note.octave.value}.wav"
    IO.println(s"Velocity: $note") >>
      SimpleSamplePlayer().playSample(filePath, note, when)

  override def play(
      musicEvent: AtomicMusicalEvent,
      when: Double,
      attack: Attack,
      release: Release,
      tempo: Tempo)(using audioContext: dom.AudioContext): IO[Unit] =
    musicEvent match
      case note: Note =>
        playSingleNote(note, when)
      case harmony: Harmony =>
        harmony
          .notes
          .toList
          .parTraverse(harmonyTiming =>
            harmonyTiming.note match
              case singleNote: Note => playSingleNote(singleNote, when)
              case _ => IO.unit
          )
          .void
      case _ => IO.unit
final case class SimpleDrums() extends Instrument:
  def play(
      musicEvent: AtomicMusicalEvent,
      when: Double,
      attack: Attack,
      release: Release,
      tempo: Tempo)(using audioContext: dom.AudioContext): IO[Unit] =
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
final case class SimpleDrumSynth() extends Instrument:
  def play(
      musicEvent: AtomicMusicalEvent,
      when: Double,
      attack: Attack,
      release: Release,
      tempo: Tempo)(using audioContext: dom.AudioContext): IO[Unit] =
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

trait Synth(genOscillators: () => Seq[Oscillator])(using audioContext: AudioContext)
    extends Instrument:
  var oscillators: Seq[Oscillator] = genOscillators()

  def play(time: Double): Unit =
    oscillators.foreach(_.start(time))

  def stop(): Unit =
    oscillators.foreach(_.stop(audioContext.currentTime))
    oscillators = genOscillators()

  def stop(time: Double): Unit =
    oscillators.foreach(_.stop(time))
    oscillators = genOscillators()

  def updateFilterFrequency(f: Double): Unit =
    oscillators.foreach(_.updateFilterFrequency(f))

  def updatePitchFrequency(f: Double): Unit =
    oscillators.foreach(_.updateFrequency(f))

  def updateVolume(volume: Volume, waveType: WaveType): Unit =
    oscillators.foreach(_.updateVolume(volume))

  def attackRelease(when: Double, note: Note, tempo: Tempo, release: Release): IO[Unit] =
    IO:
      val keyNote = note.frequency
      this.updatePitchFrequency(keyNote)
      this.play(when)
      this.stop(when + note.durationToSeconds(tempo))

  def play(
      musicEvent: AtomicMusicalEvent,
      when: Double,
      attack: Attack,
      release: Release,
      tempo: Tempo)(using audioContext: dom.AudioContext): IO[Unit] =
    musicEvent match
      case note: AtomicMusicalEvent.Note =>
        this.attackRelease(when, note, tempo, release)
      case chord: AtomicMusicalEvent.Harmony =>
        chord.notes.parTraverse(note => play(note.note, when, attack, release, tempo)).void
      case _ => IO.unit

end Synth

object Synth:
  def apply()(using audioContext: AudioContext): Synth = ScalaSynth()
  def default()(using audioContext: AudioContext): Synth = Synth()
  def simpleSine()(using audioContext: AudioContext): Synth = ScalaSynth()
  def simpleSawtooth()(using audioContext: AudioContext): Synth = SawtoothSynth()
end Synth

/**
 * A simple example synthesizer that combines 3 oscillators at different octaves
 * @param audioContext
 */
final case class ScalaSynth()(using audioContext: AudioContext)
    extends Synth(() => ScalaSynth.genOscillators)(using audioContext: AudioContext)
object ScalaSynth:
  private def genOscillators(using audioContext: AudioContext): Seq[Oscillator] = Seq(
    SineOscillator(),
    TriangleOscillator(frequencyModifier = _ * 2, volumeModifier = _ / 4)
  )
end ScalaSynth

final case class SineSynth()(using audioContext: AudioContext)
    extends Synth(() => SineSynth.genOscillators)(using audioContext: AudioContext)

object SineSynth:
  private def genOscillators(using audioContext: AudioContext): Seq[Oscillator] = Seq(
    SineOscillator()
  )
end SineSynth

final case class SawtoothSynth()(using audioContext: AudioContext)
    extends Synth(() => SawtoothSynth.genOscillators)(using audioContext: AudioContext)

object SawtoothSynth:
  private def genOscillators(using audioContext: AudioContext): Seq[Oscillator] = Seq(
    SawtoothOscillator(volumeModifier = _ / 4)
  )
end SawtoothSynth

final case class PianoSynth()(using audioContext: AudioContext)
    extends Synth(() => PianoSynth.genOscillators)(using audioContext: AudioContext)

object PianoSynth:
  private def sineFrequencyModifier = (f: Double) => f * 2
  private def triangleVelocityModifier = (v: Double) => v / 8
  private def genOscillators(using audioContext: AudioContext): Seq[Oscillator] = Seq(
    SineOscillator(frequencyModifier = sineFrequencyModifier),
    TriangleOscillator(volumeModifier = triangleVelocityModifier)
  )
end PianoSynth

object TestSynth:
  private def genOscillators()(using audioContext: AudioContext): Seq[Oscillator] = Seq(
    SineOscillator(frequencyModifier = _ * 8),
    SawtoothOscillator(frequencyModifier = _ * 2),
    SquareOscillator(),
    TriangleOscillator(frequencyModifier = _ - 3)
  )
end TestSynth

case class TestSynth()(using audioContext: AudioContext)
    extends Synth(() => TestSynth.genOscillators())(using audioContext: AudioContext)

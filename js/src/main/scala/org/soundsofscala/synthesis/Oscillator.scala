package org.soundsofscala.synthesis

import org.scalajs.dom.{AudioContext, OscillatorNode}
import org.soundsofscala.models.*

enum WaveType:
  case Sine, Square, Sawtooth, Triangle

/**
 * Oscillator is a sound wave generator that can be played at a certain frequency and volume. This
 * is the fundamental building block of sound synthesis.
 *
 * It can be of different types, such as Sine, Square, Sawtooth, and Triangle. The oscillator can be
 * played, stopped, and updated with a new frequency and volume.
 */

object Oscillator:
  def apply(
      waveType: WaveType,
      frequency: Frequency,
      volume: Volume
  ): AudioContext ?=> Oscillator =
    waveType match
      case WaveType.Sine => SineOscillator(frequency, volume)
      case WaveType.Square => SquareOscillator(frequency, volume)
      case WaveType.Sawtooth => SawtoothOscillator(frequency, volume)
      case WaveType.Triangle => TriangleOscillator(frequency, volume)

  def stringToWaveType(waveType: String): WaveType =
    waveType match
      case "Sine" => WaveType.Sine
      case "Square" => WaveType.Square
      case "Sawtooth" => WaveType.Sawtooth
      case "Triangle" => WaveType.Triangle

enum Oscillator(frequency: Frequency, volume: Volume)(using audioContext: AudioContext):
  private val oscillatorNode: OscillatorNode = audioContext.createOscillator()
  private val amplifier = Amplifier()
  private val lowPassFilter = Filter.LowPass(Frequency(5000), Bandwidth(1))
  oscillatorNode.frequency.value = frequency.value
  oscillatorNode.`type` = this match
    case _: SineOscillator => "sine"
    case _: SquareOscillator => "square"
    case _: SawtoothOscillator => "sawtooth"
    case _: TriangleOscillator => "triangle"

  def updateFilterFrequency(frequency: Double): Unit = lowPassFilter.updateF(frequency)
  def updateFrequency(frequency: Double): Unit = oscillatorNode.frequency.value = frequency
  def updateVolume(volume: Volume): Unit =
    amplifier.setLevelIndiscriminately(volume)

  def play(when: Double): Unit =
    amplifier.level(volume, when)
    amplifier.plugIn(lowPassFilter.plugIn(oscillatorNode))
    amplifier.plugInTo(audioContext.destination)
    oscillatorNode.start(when)

  def start(when: Double): Unit =
    amplifier.plugIn(lowPassFilter.plugIn(oscillatorNode))
    amplifier.plugInTo(audioContext.destination)
    oscillatorNode.start(when)

  def stop(when: Double): Unit =
    amplifier.quickFade(when: Double)
    oscillatorNode.stop(when + 10)

  def volume(volume: Volume): Oscillator =
    this match
      case SineOscillator(freq, _) => SineOscillator(freq, volume)
      case SquareOscillator(freq, _) => SquareOscillator(freq, volume)
      case SawtoothOscillator(freq, _) => SawtoothOscillator(freq, volume)
      case TriangleOscillator(freq, _) => TriangleOscillator(freq, volume)

  def frequency(newFrequency: Frequency): Oscillator =
    this match
      case SineOscillator(_, vol) => SineOscillator(newFrequency, vol)
      case SquareOscillator(_, vol) => SquareOscillator(newFrequency, vol)
      case SawtoothOscillator(_, vol) => SawtoothOscillator(newFrequency, vol)
      case TriangleOscillator(_, vol) => TriangleOscillator(newFrequency, vol)

  private def waveTypeToString(oscillator: Oscillator): String =
    oscillator match
      case _: SineOscillator => "sine"
      case _: SquareOscillator => "square"
      case _: SawtoothOscillator => "sawtooth"
      case _: TriangleOscillator => "triangle"

  case SineOscillator(frequency: Frequency = Frequency(440), volume: Volume = Volume(0.3))(
      using AudioContext) extends Oscillator(frequency, volume)

  case SquareOscillator(frequency: Frequency = Frequency(440), volume: Volume = Volume(0.3))(
      using AudioContext) extends Oscillator(frequency, volume)

  case SawtoothOscillator(frequency: Frequency = Frequency(440), volume: Volume = Volume(0.3))(
      using AudioContext) extends Oscillator(frequency, volume)

  case TriangleOscillator(frequency: Frequency = Frequency(440), volume: Volume = Volume(0.3))(
      using AudioContext) extends Oscillator(frequency, volume)
end Oscillator

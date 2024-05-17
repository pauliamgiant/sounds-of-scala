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
      volume: Volume,
      frequencyModifier: Double => Double = identity,
      volumeModifier: Double => Double = identity
  ): AudioContext ?=> Oscillator =
    waveType match
      case WaveType.Sine => SineOscillator(frequency, volume, frequencyModifier, volumeModifier)
      case WaveType.Square => SquareOscillator(frequency, volume, frequencyModifier, volumeModifier)
      case WaveType.Sawtooth =>
        SawtoothOscillator(frequency, volume, frequencyModifier, volumeModifier)
      case WaveType.Triangle =>
        TriangleOscillator(frequency, volume, frequencyModifier, volumeModifier)

  def stringToWaveType(waveType: String): WaveType =
    waveType match
      case "Sine" => WaveType.Sine
      case "Square" => WaveType.Square
      case "Sawtooth" => WaveType.Sawtooth
      case "Triangle" => WaveType.Triangle
end Oscillator

enum Oscillator(
    private val frequency: Frequency,
    private val volume: Volume,
    frequencyModifier: Double => Double,
    volumeModifier: Double => Double)(using audioContext: AudioContext):
  private val oscillatorNode: OscillatorNode = audioContext.createOscillator()
  private val amplifier = Amplifier()
  private val lowPassFilter = Filter.LowPass(Frequency(5000), Bandwidth(1))
  oscillatorNode.frequency.value = frequencyModifier(frequency.value)
  oscillatorNode.`type` = this match
    case _: SineOscillator => "sine"
    case _: SquareOscillator => "square"
    case _: SawtoothOscillator => "sawtooth"
    case _: TriangleOscillator => "triangle"

  /**
   * Updates the frequency of the filter, conditionally respecting the filter's custom frequency
   * modifier
   * @param frequency
   *   the new frequency before any modifications
   * @param bypassFrequencyModifier
   *   if true, the frequency modifier will not be applied
   */
  def updateFilterFrequency(frequency: Double, bypassFrequencyModifier: Boolean = false): Unit =
    if bypassFrequencyModifier then
      lowPassFilter.updateF(frequency)
    else
      lowPassFilter.updateF(frequencyModifier(frequency))

  /**
   * Updates the frequency of the oscillator, conditionally respecting the oscillator's custom
   * frequency modifier
   * @param frequency
   *   the new frequency before any modifications
   * @param bypassFrequencyModifier
   *   if true, the frequency modifier will not be applied
   */
  def updateFrequency(frequency: Double, bypassFrequencyModifier: Boolean = false): Unit =
    oscillatorNode.frequency.value =
      if bypassFrequencyModifier then
        frequency
      else
        frequencyModifier(frequency)

  /**
   * Updates the volume of the oscillator, conditionally respecting the oscillator's custom volume
   * modifier
   * @param volume
   *   the new volume before any modifications
   * @param bypassVolumeModifier
   *   if true, the volume modifier will not be applied
   */
  def updateVolume(volume: Volume, bypassVolumeModifier: Boolean = false): Unit =
    if bypassVolumeModifier then
      amplifier.setLevelIndiscriminately(volume)
    else
      amplifier.setLevelIndiscriminately(Volume(volumeModifier(volume.value)))

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
      case sine: SineOscillator => sine.copy(volume = volume)
      case square: SquareOscillator => square.copy(volume = volume)
      case sawtooth: SawtoothOscillator => sawtooth.copy(volume = volume)
      case triangle: TriangleOscillator => triangle.copy(volume = volume)

  def frequency(newFrequency: Frequency): Oscillator =
    this match
      case sine: SineOscillator => sine.copy(frequency = newFrequency)
      case square: SquareOscillator => square.copy(frequency = newFrequency)
      case sawtooth: SawtoothOscillator => sawtooth.copy(frequency = newFrequency)
      case triangle: TriangleOscillator => triangle.copy(frequency = newFrequency)

  private def waveTypeToString(oscillator: Oscillator): String =
    oscillator match
      case _: SineOscillator => "sine"
      case _: SquareOscillator => "square"
      case _: SawtoothOscillator => "sawtooth"
      case _: TriangleOscillator => "triangle"

  case SineOscillator(
      frequency: Frequency = Frequency(440),
      volume: Volume = Volume(0.3),
      frequencyModifier: Double => Double = identity,
      volumeModifier: Double => Double = identity)(
      using AudioContext) extends Oscillator(frequency, volume, frequencyModifier, volumeModifier)

  case SquareOscillator(
      frequency: Frequency = Frequency(440),
      volume: Volume = Volume(0.3),
      frequencyModifier: Double => Double = identity,
      volumeModifier: Double => Double = identity)(
      using AudioContext) extends Oscillator(frequency, volume, frequencyModifier, volumeModifier)

  case SawtoothOscillator(
      frequency: Frequency = Frequency(440),
      volume: Volume = Volume(0.3),
      frequencyModifier: Double => Double = identity,
      volumeModifier: Double => Double = identity)(
      using AudioContext) extends Oscillator(frequency, volume, frequencyModifier, volumeModifier)

  case TriangleOscillator(
      frequency: Frequency = Frequency(440),
      volume: Volume = Volume(0.3),
      frequencyModifier: Double => Double = identity,
      volumeModifier: Double => Double = identity)(
      using AudioContext) extends Oscillator(frequency, volume, frequencyModifier, volumeModifier)
end Oscillator

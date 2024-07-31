/*
 * Copyright 2024 Sounds of Scala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.soundsofscala.synthesis

import org.scalajs.dom.AudioContext
import org.scalajs.dom.OscillatorNode
import org.soundsofscala.models.AudioTypes.WaveType
import org.soundsofscala.models.*

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
      frequency: Hertz,
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

enum Oscillator(frequency: Hertz, volume: Volume)(using audioContext: AudioContext):
  private val oscillatorNode: OscillatorNode = audioContext.createOscillator()
  private val amplifier = Amplifier()
  private val lowPassFilter = Filter.LowPass(Hertz(5000), Bandwidth(1))
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

  def frequency(newFrequency: Hertz): Oscillator =
    this match
      case SineOscillator(_, vol) => SineOscillator(newFrequency, vol)
      case SquareOscillator(_, vol) => SquareOscillator(newFrequency, vol)
      case SawtoothOscillator(_, vol) => SawtoothOscillator(newFrequency, vol)
      case TriangleOscillator(_, vol) => TriangleOscillator(newFrequency, vol)

  case SineOscillator(frequency: Hertz = Hertz(440), volume: Volume = Volume(0.3))(
      using AudioContext) extends Oscillator(frequency, volume)

  case SquareOscillator(frequency: Hertz = Hertz(440), volume: Volume = Volume(0.3))(
      using AudioContext) extends Oscillator(frequency, volume)

  case SawtoothOscillator(frequency: Hertz = Hertz(440), volume: Volume = Volume(0.3))(
      using AudioContext) extends Oscillator(frequency, volume)

  case TriangleOscillator(frequency: Hertz = Hertz(440), volume: Volume = Volume(0.3))(
      using AudioContext) extends Oscillator(frequency, volume)
end Oscillator

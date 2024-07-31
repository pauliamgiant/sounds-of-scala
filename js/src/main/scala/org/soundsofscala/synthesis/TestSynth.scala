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
import org.soundsofscala.models.AudioTypes.WaveType
import org.soundsofscala.models.*
import org.soundsofscala.synthesis.Oscillator.*

case class TestSynth()(using audioContext: AudioContext):
  val startingF = 440

  var oscillators: List[Oscillator] = List(
    SineOscillator(Hertz(440)),
    SawtoothOscillator(Hertz(startingF / 8)),
    SquareOscillator().frequency(Hertz(startingF / 4)),
    TriangleOscillator().frequency(Hertz(startingF - 3))
  )

  def play(time: Double): Unit =
    oscillators.foreach(_.start(time))

  def stop(): Unit =
    oscillators.foreach(_.stop(audioContext.currentTime))
    oscillators = List(
      SineOscillator(Hertz(440)),
      SawtoothOscillator(Hertz(startingF / 8)),
      SquareOscillator().frequency(Hertz(startingF / 4)),
      TriangleOscillator().frequency(Hertz(startingF - 3))
    )

  def updateFilterFrequency(f: Double): Unit =
    oscillators.head.updateFilterFrequency(f)
    oscillators(1).updateFilterFrequency(f)
    oscillators(2).updateFilterFrequency(f)
    oscillators(3).updateFilterFrequency(f)

  def updatePitchFrequency(f: Double): Unit =
    oscillators.head.updateFrequency(f)
    oscillators(1).updateFrequency(f / 4)
    oscillators(2).updateFrequency(f / 2)
    oscillators(3).updateFrequency(f - 3)

  def updateVolume(volume: Volume, waveType: WaveType): Unit =
    waveType match
      case WaveType.Sine =>
        oscillators.head.updateVolume(volume)
      case WaveType.Sawtooth =>
        oscillators(1).updateVolume(volume)
      case WaveType.Square =>
        oscillators(2).updateVolume(volume)
      case WaveType.Triangle =>
        oscillators(3).updateVolume(volume)
end TestSynth

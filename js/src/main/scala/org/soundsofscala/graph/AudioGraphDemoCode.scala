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

package org.soundsofscala.graph

import cats.effect.IO
import org.scalajs.dom.*
import org.soundsofscala.graph.AudioNode.{Gain, bandPassFilter, sawtoothOscillator}
import org.soundsofscala.graph.AudioParam.AudioParamEvent.SetValueAtTime

object AudioGraphDemoCode:
  def buildAudioGraphDemo(using ac: AudioContext): IO[Unit] = IO {

    val initialTime = ac.currentTime

    val noteLength = 12.0

    val listOfNotePitchFrequencies = Vector(220, 110, 55, 110, 220, 440, 880, 440, 220, 110,
      55, 110, 220, 440, 880, 440, 220, 110, 55, 110, 220, 440, 880, 440, 220)

    // We are creating frequency change events every 0.5 seconds
    val pitchList = listOfNotePitchFrequencies.zipWithIndex.map {
      case (freq, idx) =>
        SetValueAtTime(freq, initialTime + (idx * 0.5))
    } :+ SetValueAtTime(220, initialTime + noteLength)

    // build a sawtooth oscillator using the list of changing pitches

    val sawToothOsc =
      sawtoothOscillator(initialTime, noteLength).withFrequency(AudioParam(pitchList))

    // This is an example of a band pass filter with a frequency sweep
    // We use the Audioparam to change the frequency of the band pass filter over time

    val startFrequency = 100
    val endFrequency = 13500
    val steps = 25

    val frequencies: Vector[Double] = (0 until steps).map { index =>
      startFrequency * Math.pow(
        endFrequency.toDouble / startFrequency,
        index.toDouble / (steps - 1))
    }.toVector

    // build the band pass filter

    val bandpass = bandPassFilter.withFrequency(
      AudioParam(generateSetValueAtTime(
        initialTime = 0,
        timeStep = 0.5,
        valuesPattern = frequencies,
        endTime = noteLength))
    )

    // build the gain node and have the volume change over time to create a tremolo effect
    val valuesPattern = Vector(0.0, 0.5, 0.1, 0.5)

    val gainNode =
      Gain(
        List.empty,
        AudioParam(generateSetValueAtTime(
          initialTime = initialTime,
          timeStep = 1.0,
          valuesPattern = valuesPattern,
          endTime = noteLength)))

    // connect the nodes
    val graph = sawToothOsc --> bandpass --> gainNode

    // play the graph
    graph.play
  }
  end buildAudioGraphDemo

  private def generateSetValueAtTime(
      initialTime: Double,
      timeStep: Double,
      valuesPattern: Vector[Double],
      endTime: Double): Vector[SetValueAtTime] =

    val numberOfSteps = ((endTime - initialTime) / timeStep).toInt + 1
    (0 until numberOfSteps).toVector.map: i =>
      val value = valuesPattern(i % valuesPattern.size)
      val time = initialTime + (i * timeStep)
      SetValueAtTime(value, time)
end AudioGraphDemoCode

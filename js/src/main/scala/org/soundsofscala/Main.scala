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

package org.soundsofscala

import cats.effect.unsafe.implicits.global
import org.scalajs.dom
import org.scalajs.dom.*
import org.soundsofscala.graph.AudioNode.*
import org.soundsofscala.graph.AudioParam
import org.soundsofscala.graph.AudioParam.AudioParamEvent
import org.soundsofscala.graph.AudioParam.AudioParamEvent.SetValueAtTime
import org.soundsofscala.songexamples.*

object Main extends App:

  document.addEventListener(
    "DOMContentLoaded",
    (e: dom.Event) =>

      val homeDiv = document.createElement("div")
      homeDiv.classList.add("home-div")

      val heading = document.createElement("h1")
      heading.textContent = "Sounds of Scala"

      val exampleWebAppLabel = document.createElement("h2")
      exampleWebAppLabel.textContent = "Test section - Place your code here:"

      val buttonWrapper = document.createElement("div")
      buttonWrapper.classList.add("button-pad")
      val actionButtonDiv = document.createElement("button")
      actionButtonDiv.textContent = "️▶️"
      actionButtonDiv.addEventListener(
        "click",
        (_: dom.MouseEvent) =>
          given audioContext: AudioContext = new AudioContext()
          // your actions here
          ExampleSong2.play().unsafeRunAndForget()
      )

      val audioGraphButtonWrapper = document.createElement("div")
      audioGraphButtonWrapper.classList.add("button-pad")
      val audioGraphButton = document.createElement("button")
      audioGraphButton.textContent = "️▶️"
      audioGraphButton.addEventListener(
        "click",
        (_: dom.MouseEvent) =>
          given audioContext: AudioContext = new AudioContext()
          // your actions here
          ExampleSong2.play().unsafeRunAndForget()
          val startFrequency = 220.0
          val endFrequency = 110.0
          val step = 5.0
          val initialTime = audioContext.currentTime
          val timeStep = 0.2

          val descendingValues: Vector[AudioParamEvent] =
            createSetValueAtTime(startFrequency, endFrequency, step, initialTime, timeStep)
          val finalFrequencies = (110 to 220 by 5).toVector
          val finalTimes = finalFrequencies.indices.toVector.map(i =>
            initialTime + ((startFrequency - endFrequency) / step + 1) * timeStep + i * timeStep)
          val ascendingValues =
            finalFrequencies.zip(finalTimes).map { case (freq, time) => SetValueAtTime(freq, time) }

          val valuesPattern = Vector(0.0, 0.5, 0.1, 0.5)

          console.log("Button clicked")
          val sawOscillator =
            sawtooth.withFrequency(AudioParam(descendingValues ++ ascendingValues))

          // build a band pass filter
          val bandpass = bandPassFilter.withFrequency(
            AudioParam(filterEvents))

          // build a gain node
          val gainNode =
            Gain(
              List.empty,
              AudioParam(generateSetValueAtTime(initialTime, timeStep, valuesPattern, 20.0)))

          // connect the nodes
          val graph = sawOscillator --> bandpass --> gainNode

          // create the graph
          graph.create
      )

      val clickToRun = document.createElement("h3")
      clickToRun.textContent = "Click to run your code"

      buttonWrapper.append(actionButtonDiv)
      audioGraphButtonWrapper.append(audioGraphButton)

      document.createElement("hr")

      homeDiv.append(
        heading,
        exampleWebAppLabel,
        buttonWrapper,
        audioGraphButtonWrapper,
        clickToRun,
        document.createElement("hr")
      )
      document.body.appendChild(homeDiv)
  )
end Main

private def createSetValueAtTime(
    start: Double,
    end: Double,
    step: Double,
    initialTime: Double,
    timeStep: Double): Vector[AudioParamEvent] =
  (0 to ((start - end) / step).toInt).toVector.map: i =>
    val frequency = start - (i * step)
    val time = initialTime + (i * timeStep)
    SetValueAtTime(frequency, time)

val filterEvents = Vector(
  AudioParamEvent.SetValueAtTime(100, 0),
  AudioParamEvent.SetValueAtTime(200, 0.5),
  AudioParamEvent.SetValueAtTime(300, 1),
  AudioParamEvent.SetValueAtTime(400, 1.5),
  AudioParamEvent.SetValueAtTime(500, 2),
  AudioParamEvent.SetValueAtTime(600, 2.5),
  AudioParamEvent.SetValueAtTime(700, 3),
  AudioParamEvent.SetValueAtTime(800, 3.5),
  AudioParamEvent.SetValueAtTime(900, 4),
  AudioParamEvent.SetValueAtTime(1000, 4.5),
  AudioParamEvent.SetValueAtTime(1500, 5),
  AudioParamEvent.SetValueAtTime(2000, 5.5),
  AudioParamEvent.SetValueAtTime(2500, 6),
  AudioParamEvent.SetValueAtTime(3000, 6.5),
  AudioParamEvent.SetValueAtTime(3500, 7),
  AudioParamEvent.SetValueAtTime(4000, 7.5),
  AudioParamEvent.SetValueAtTime(4500, 8),
  AudioParamEvent.SetValueAtTime(5000, 8.5),
  AudioParamEvent.SetValueAtTime(5500, 9),
  AudioParamEvent.SetValueAtTime(6000, 9.5)
)

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

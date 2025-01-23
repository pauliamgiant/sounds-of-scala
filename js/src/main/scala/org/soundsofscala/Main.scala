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
import org.soundsofscala.graph.AudioNode.squareOscillator
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
          ExampleSong5.play().unsafeRunAndForget()
      )

      val audioGraphButtonWrapper = document.createElement("div")
      audioGraphButtonWrapper.classList.add("button-pad")
      val audioGraphButton = document.createElement("button")
      audioGraphButton.textContent = "️▶️"
      audioGraphButton.addEventListener(
        "click",
        (_: dom.MouseEvent) =>
          console.log("Button clicked")
          given audioContext: AudioContext = new AudioContext()

          val initialTime = audioContext.currentTime

          val noteList = Vector(
            SetValueAtTime(220, initialTime),
            SetValueAtTime(110, initialTime + 0.5),
            SetValueAtTime(55, initialTime + 1),
            SetValueAtTime(110, initialTime + 1.5),
            SetValueAtTime(220, initialTime + 2),
            SetValueAtTime(440, initialTime + 2.5),
            SetValueAtTime(880, initialTime + 3),
            SetValueAtTime(440, initialTime + 3.5),
            SetValueAtTime(220, initialTime + 4),
            SetValueAtTime(110, initialTime + 4.5),
            SetValueAtTime(55, initialTime + 5),
            SetValueAtTime(110, initialTime + 5.5),
            SetValueAtTime(220, initialTime + 6),
            SetValueAtTime(440, initialTime + 6.5),
            SetValueAtTime(880, initialTime + 7),
            SetValueAtTime(440, initialTime + 7.5),
            SetValueAtTime(220, initialTime + 8),
            SetValueAtTime(110, initialTime + 8.5),
            SetValueAtTime(55, initialTime + 9),
            SetValueAtTime(110, initialTime + 9.5),
            SetValueAtTime(220, initialTime + 10),
            SetValueAtTime(440, initialTime + 10.5),
            SetValueAtTime(880, initialTime + 11),
            SetValueAtTime(440, initialTime + 11.5),
            SetValueAtTime(220, initialTime + 12)
          )

          // build a sawtooth oscillator using noteList
          val sqOsc =
            squareOscillator(initialTime, 0.5).withFrequency(AudioParam(noteList))

          // build a band pass filter
          val bandpass = bandPassFilter.withFrequency(
            AudioParam(Vector(
              SetValueAtTime(100, 0),
              SetValueAtTime(200, 0.5),
              SetValueAtTime(300, 1),
              SetValueAtTime(400, 1.5),
              SetValueAtTime(500, 2),
              SetValueAtTime(600, 2.5),
              SetValueAtTime(700, 3),
              SetValueAtTime(800, 3.5),
              SetValueAtTime(900, 4),
              SetValueAtTime(1000, 4.5),
              SetValueAtTime(1500, 5),
              SetValueAtTime(2000, 5.5),
              SetValueAtTime(2500, 6),
              SetValueAtTime(3000, 6.5),
              SetValueAtTime(3500, 7),
              SetValueAtTime(4000, 7.5),
              SetValueAtTime(4500, 8),
              SetValueAtTime(5000, 8.5),
              SetValueAtTime(5500, 9),
              SetValueAtTime(6000, 9.5),
              SetValueAtTime(7500, 10),
              SetValueAtTime(9000, 10.5),
              SetValueAtTime(10500, 11),
              SetValueAtTime(12000, 11.5),
              SetValueAtTime(13500, 12)
            )))

          // build a gain node
          val valuesPattern = Vector(0.0, 0.5, 0.1, 0.5)

          val gainNode =
            Gain(
              List.empty,
              AudioParam(generateSetValueAtTime(initialTime, 0.2, valuesPattern, 20.0)))

          // connect the nodes
          val graph = sqOsc --> bandpass --> gainNode

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

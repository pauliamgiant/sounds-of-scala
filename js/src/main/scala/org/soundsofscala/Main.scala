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

      val logoImage = document.createElement("img")
      logoImage.setAttribute("src", "resources/images/sounds_of_scala_logo.png")
      logoImage.setAttribute("alt", "Sounds of Scala Logo")
      logoImage.setAttribute("style", "width: 8em; height: auto; display: block; margin: 0 auto;")

      homeDiv.append(logoImage)
      val heading = document.createElement("h1")
      heading.textContent = "Welcome to Sounds of Scala"

      val exampleWebAppLabel = document.createElement("h2")
      exampleWebAppLabel.textContent =
        "Test section - Quickstart - You can run this Main method locally to try out the library features and example songs."

      val exampleMoreInfoText = document.createElement("p")
      exampleMoreInfoText.textContent =
        "Click the First play button here to play ExampleSong1 you'll find in scala/org/soundsofscala/songexamples."

      val beethovenText = document.createElement("p")
      beethovenText.textContent =
        "Click the ExampleSong4Beethoven button to hear WaveTable synthesis."

      val thingsToTryLabel = document.createElement("h2")
      thingsToTryLabel.classList.add("left-align")
      thingsToTryLabel.textContent = "Things to try"

      val exampleList = document.createElement("ul")

      val listItem1 = document.createElement("li")
      listItem1.textContent = "Try editing the Song in the ExampleSong1 class."
      exampleList.append(listItem1)

      val listItem2 = document.createElement("li")
      listItem2.textContent =
        "Try swapping out ExampleSong1 on line 83 with one of the other Song Examples in the songexamples package."
      exampleList.append(listItem2)

      val listItem3 = document.createElement("li")
      listItem3.textContent = "Try creating your own Song file."
      exampleList.append(listItem3)

      val listItem4 = document.createElement("li")
      listItem4.textContent =
        "You can also try out the AudioGraph example by clicking the second play button below. This will create a band pass filter with a frequency sweep and a gain node with a tremolo effect. You can change the parameters in the code to create different sounds."
      exampleList.append(listItem4)

      val buttonWrapper = document.createElement("div")
      buttonWrapper.classList.add("button-pad")
      val actionButtonDiv = document.createElement("button")
      actionButtonDiv.textContent = "ExampleSong1 ▶︎"
      actionButtonDiv.addEventListener(
        "click",
        (_: dom.MouseEvent) =>
          given audioContext: AudioContext = new AudioContext()
          // For a quick start create a song file and replace this Example song here
          ExampleSong1.play().unsafeRunAndForget()
      )

      val beethovenButtonWrapper = document.createElement("div")
      beethovenButtonWrapper.classList.add("button-pad")
      val beethovenButtonDiv = document.createElement("button")
      beethovenButtonDiv.textContent = "ExampleSong4Beethoven ▶︎"
      beethovenButtonDiv.addEventListener(
        "click",
        (_: dom.MouseEvent) =>
          given audioContext: AudioContext = new AudioContext()
          // For a quick start create a song file and replace this Example song here
          ExampleSong4Beethoven.play().unsafeRunAndForget()
      )

      val audioGraphButtonWrapper = document.createElement("div")
      audioGraphButtonWrapper.classList.add("button-pad")
      val audioGraphButton = document.createElement("button")
      audioGraphButton.textContent = "Audio Graph in action ▶︎"
      audioGraphButton.addEventListener(
        "click",
        (_: dom.MouseEvent) =>
          given audioContext: AudioContext = new AudioContext()

          val initialTime = audioContext.currentTime

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
                timeStep = 0.05,
                valuesPattern = valuesPattern,
                endTime = noteLength)))

          // connect the nodes
          val graph = sawToothOsc --> bandpass --> gainNode

          // create the graph
          graph.create
      )

      buttonWrapper.append(actionButtonDiv)
      audioGraphButtonWrapper.append(audioGraphButton)
      beethovenButtonWrapper.append(beethovenButtonDiv)

      document.createElement("hr")

      homeDiv.append(
        heading,
        exampleWebAppLabel,
        exampleMoreInfoText,
        buttonWrapper,
        document.createElement("hr"),
        beethovenText,
        beethovenButtonWrapper,
        document.createElement("hr"),
        thingsToTryLabel,
        exampleList,
        audioGraphButtonWrapper,
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

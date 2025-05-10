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
import cats.effect.{ExitCode, IO, IOApp}
import org.scalajs.dom
import org.scalajs.dom.*
import org.soundsofscala.graph.AudioGraphDemoCode
import org.soundsofscala.models.FilePath
import org.soundsofscala.playback.AudioPlayer
import org.soundsofscala.songexamples.*

object Main extends IOApp:

  def run(args: List[String]): IO[ExitCode] =
    IO {
      document.addEventListener(
        "DOMContentLoaded",
        (_: dom.Event) =>
          given AudioContext = new AudioContext()

          setupPage().unsafeRunAndForget()
      )
    }.as(ExitCode.Success)

  private def setupPage(): AudioContext ?=> IO[Unit] =
    for
      audioPlayer <- AudioPlayer(FilePath("resources/audio/sounds-of-scala.mp3"))

      homeDiv <- IO(document.createElement("div"))
      _ <- IO(homeDiv.classList.add("home-div"))
      logoImage <- buildLogo
      heading <- buildHeading
      quickStart <- buildSimpleAudioPlayerDirections
      introText <- buildIntroductionText
      beethovenText <- buildBeethovenText
      audioPlayerText <- buildAudioplayerText
      thingsToTry <- buildThingsToTry()

      playButton <- buildButton(label = "▶︎", buttonAction = audioPlayer.play())
      stopButton <- buildButton(label = "◼︎", buttonAction = audioPlayer.stop())
      pauseButton <- buildButton(label = "⏸︎", buttonAction = audioPlayer.pause())
      playExampleSong1Button <- buildButton(
        label = "▶︎ ExampleSong1",
        buttonAction = ExampleSong1.play()
      )
      beethovenButton <- buildButton(
        label = "▶︎ ExampleSong4Beethoven",
        buttonAction = ExampleSong5Beethoven.play()
      )
      audioGraphButton <- buildButton(
        label = "Audio Graph in action ▶",
        buttonAction = AudioGraphDemoCode.buildAudioGraphDemo
      )

      transportDiv <- buildTransport(playButton, stopButton, pauseButton)
      _ <- addElementsToHomeDiv(
        logoImage,
        heading,
        quickStart,
        document.createElement("hr"),
        introText,
        playExampleSong1Button,
        document.createElement("hr"),
        beethovenText,
        beethovenButton,
        document.createElement("hr"),
        audioPlayerText,
        transportDiv,
        document.createElement("hr"),
        thingsToTry,
        audioGraphButton
      )(homeDiv)
    yield ()

  private def buildHeading = IO {
    val title = document.createElement("h1")
    title.textContent = "Welcome to Sounds of Scala"
    title
  }

  private def buildSimpleAudioPlayerDirections: IO[dom.Element] = IO {

    val div = document.createElement("div")

    val title = document.createElement("h1")
    title.textContent = "Make sound fast with the Simple Audio Player"

    val fastestWayText = document.createElement("p")
    fastestWayText.textContent =
      "The fastest way to make some sound emanate from your device is with the SimpleAudioPlayer."

    val addToSbt = document.createElement("p")
    addToSbt.textContent = "Add the following to your build.sbt file:"

    val codeStringSbt: String =
      """|
         |"libraryDependencies += "org.soundsofscala" %%% "sounds-of-scala" % "0.3.1""".stripMargin

    val addToCode = document.createElement("p")
    addToCode.textContent =
      "Then in your code pass the path to the audio file to the SimpleAudioPlayer constructor."

    val codeString: String =
      """|val audioPlayer = SimpleAudioPlayer("<PATH_TO_LOCAL_AUDIO_FILE>")
         |audioPlayer.play()
         |audioPlayer.pause()
         |audioPlayer.play()
         |audioPlayer.stop()
         |""".stripMargin

    val preSbt = document.createElement("pre").asInstanceOf[dom.HTMLPreElement]
    val preCode = document.createElement("pre").asInstanceOf[dom.HTMLPreElement]
    val codeSbt = document.createElement("code").asInstanceOf[dom.HTMLPreElement]
    val code = document.createElement("code").asInstanceOf[dom.HTMLPreElement]
    codeSbt.classList.add("language-scala")
    code.classList.add("language-scala")
    codeSbt.textContent = codeStringSbt
    code.textContent = codeString
    preSbt.append(codeSbt)
    preCode.append(code)
    div.append(title, fastestWayText, addToSbt, preSbt, addToCode, preCode)
    div
  }

  private def buildLogo = IO {
    val logoImage = document.createElement("img")
    logoImage.setAttribute("src", "resources/images/sounds_of_scala_logo.png")
    logoImage.setAttribute("alt", "Sounds of Scala Logo")
    logoImage.setAttribute("style", "width: 8em; height: auto; display: block; margin: 0 auto;")
    logoImage
  }

  private def buildAudioplayerText = IO {
    val audioPlayerText = document.createElement("p")
    audioPlayerText.textContent =
      "This demos the AudioPlayer for playing, pausing, and stopping an audio file."
    audioPlayerText
  }

  private def buildBeethovenText = IO {
    val beethovenText = document.createElement("p")
    beethovenText.textContent =
      "Click the ExampleSong4Beethoven button to hear an example of WaveTable synthesis used to create an Electric Piano sound."
    beethovenText
  }

  private def buildTransport(buttons: Element*) = IO {
    val div = document.createElement("div")
    div.classList.add("transport")
    buttons.foreach(div.appendChild)
    div
  }

  private def addElementsToHomeDiv(elements: Element*)(homeDiv: Element) = IO {
    elements.foreach(homeDiv.append(_))
    document.body.appendChild(homeDiv)
  }

  private def buildIntroductionText = IO {
    val textDiv = document.createElement("div")

    val exampleWebAppLabel = document.createElement("h2")
    exampleWebAppLabel.textContent =
      "Test section - Quickstart - You can run this Main method locally to try out the library features and example songs."

    val exampleMoreInfoText = document.createElement("p")
    exampleMoreInfoText.textContent =
      "Click the play button here to play ExampleSong1 which you'll find in scala/org/soundsofscala/songexamples."
    textDiv.append(exampleWebAppLabel, exampleMoreInfoText)
    textDiv
  }

  private def buildThingsToTry() =
    IO {
      val thingsToTryDiv = document.createElement("div")
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
        "You can also try out the AudioGraph example by clicking the play button below. This will create a band pass filter with a frequency sweep and a gain node with a tremolo effect. You can change the parameters in the code to create different sounds."
      exampleList.append(listItem4)
      thingsToTryDiv.append(
        thingsToTryLabel,
        exampleList
      )
      thingsToTryDiv
    }

  private def buildButton(label: String, buttonAction: IO[Unit]): IO[Element] =
    for
      buttonContainer <- IO(document.createElement("div"))
      _ <- IO(buttonContainer.classList.add("button-pad"))
      button <- IO(document.createElement("button"))
      _ <- IO {
        button.textContent = label
        button.addEventListener("click", (_: dom.MouseEvent) => buttonAction.unsafeRunAndForget())
        buttonContainer.appendChild(button)
      }
    yield buttonContainer

end Main

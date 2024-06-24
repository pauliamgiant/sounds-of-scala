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
          ExampleSong1.play().unsafeRunAndForget()
      )

      val clickToRun = document.createElement("h3")
      clickToRun.textContent = "Click to run your code"

      buttonWrapper.append(actionButtonDiv)

      document.createElement("hr")

      homeDiv.append(
        heading,
        exampleWebAppLabel,
        buttonWrapper,
        clickToRun,
        document.createElement("hr")
      )
      document.body.appendChild(homeDiv)
  )
end Main

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

package org.soundsofscala.transport

import cats.effect.IO
import org.scalajs.dom
import org.scalajs.dom.{AudioContext, XMLHttpRequest}
import org.soundsofscala.models.{AtomicMusicalEvent, FileLoadingError}

import scala.scalajs.js.typedarray.ArrayBuffer

case class SimpleSamplePlayer()(using audioContext: AudioContext):

  def playSample(filePath: String, musicEvent: AtomicMusicalEvent, when: Double): IO[Unit] =
    for
      _ <- IO.println(s"PLAYING $filePath at $when")
      _ <- IO.println(s"Volume ${musicEvent.normalizedVelocity}")
      request <- IO(dom.XMLHttpRequest())
      _ <- IO(request.open("GET", filePath, true))
      _ <- IO(request.responseType = "arraybuffer")
      _ <- loadAndPlaySample(request, musicEvent, when)
      _ <- IO(request.send())
    yield ()

  private def loadAndPlaySample(
      request: XMLHttpRequest,
      musicalEvent: AtomicMusicalEvent,
      when: Double): IO[Unit] =
    for
      gainNode <- IO(audioContext.createGain())
      sourceNode <- IO(audioContext.createBufferSource())
      _ <- IO(gainNode.gain.value = musicalEvent.normalizedVelocity / 2)
      _ <- IO(gainNode.connect(audioContext.destination))
      _ <- IO(sourceNode.connect(gainNode))
      _ <- IO(request.onload = (_: dom.Event) =>
        val arrayBuffer: ArrayBuffer = request.response match
          case ab: ArrayBuffer => ab
        audioContext.decodeAudioData(
          arrayBuffer,
          buffer =>
            sourceNode.buffer = buffer
            sourceNode.start(when)
          ,
          () => IO.raiseError(FileLoadingError("Error decoding audio data"))
        )
      )
    yield ()
end SimpleSamplePlayer

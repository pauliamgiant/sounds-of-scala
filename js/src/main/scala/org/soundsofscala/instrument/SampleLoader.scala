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

package org.soundsofscala.instrument

import cats.effect.IO
import cats.implicits.*
import org.scalajs.dom
import org.scalajs.dom.{AudioBuffer, AudioContext, XMLHttpRequest}
import org.soundsofscala.models.FileLoadingError

import scala.scalajs.js.typedarray.ArrayBuffer

object SampleLoader:
  def loadSample(filePath: String)(using audioContext: AudioContext): IO[AudioBuffer] =
    IO.async_[AudioBuffer] { cb =>
      val request = new dom.XMLHttpRequest()
      request.open("GET", filePath, true)
      request.responseType = "arraybuffer"
      request.onerror =
        (_: dom.Event) => cb(Left(FileLoadingError(s"Request failed for $filePath")))
      request.onload = (_: dom.Event) =>
        val arrayBuffer = request.response.asInstanceOf[ArrayBuffer]
        audioContext.decodeAudioData(
          arrayBuffer,
          buffer =>
            cb(Right(buffer)),
          () => cb(Left(FileLoadingError("Error decoding audio data")))
        )
      request.send()
    }

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

import org.scalajs.dom
import org.scalajs.dom.AudioBuffer
import org.scalajs.dom.AudioContext
import org.soundsofscala.models.AtomicMusicalEvent

trait Sampler extends Instrument {}

object Sampler {
  def playSample(
      buffer: AudioBuffer,
      playbackRate: Double,
      musicalEvent: AtomicMusicalEvent,
      when: Double)(using audioContext: AudioContext): IO[Unit] =
    for
      gainNode <- IO(audioContext.createGain())
      sourceNode <- IO(audioContext.createBufferSource())
      _ <- IO(gainNode.gain.value = musicalEvent.normalizedVelocity / 2)
      _ <- IO(gainNode.connect(audioContext.destination))
      _ <- IO(sourceNode.connect(gainNode))
      _ <- IO {
        sourceNode.buffer = buffer
        sourceNode.playbackRate.value = playbackRate
        val adjustedDuration = buffer.duration * math.abs(playbackRate)
        sourceNode.start(when, 0, adjustedDuration)
      }
    yield ()
}

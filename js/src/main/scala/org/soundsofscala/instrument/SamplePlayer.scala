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
import scala.scalajs.js.typedarray.Float32Array
import scala.scalajs.js.JSConverters.iterableOnceConvertible2JSRichIterableOnce
import org.soundsofscala.playback.*

import org.soundsofscala.models.*

trait SamplePlayer extends Instrument[SamplePlayer.Settings] {}

object SamplePlayer {

  final case class Settings(
      attack: Attack,
      release: Release,
      playbackRate: Double,
      reversed: Boolean,
      loop: Option[Loop])
  object Settings {
    given Default[Settings] with {
      val default: Settings = Settings(Attack(0), Release(0.9), 1.0, false, None)
    }
  }

  def playSample(
      buffer: AudioBuffer,
      playbackRate: Double,
      musicalEvent: AtomicMusicalEvent,
      when: Double,
      settings: Settings)(using audioContext: AudioContext): IO[Unit] =

    def reverseBuffer(buffer: AudioBuffer): AudioBuffer =
      val newBuffer = audioContext.createBuffer(
        buffer.numberOfChannels,
        buffer.length,
        buffer.sampleRate.toInt
      )
      for channel <- 0 until buffer.numberOfChannels do
        val data = buffer.getChannelData(channel)
        val reversedData = data.toArray.reverse
        val typedArr = new Float32Array(reversedData.toJSArray)
        newBuffer.copyToChannel(typedArr, channel, 0)
      newBuffer

    for
      gainNode <- IO(audioContext.createGain())
      sourceNode <- IO(audioContext.createBufferSource())
      _ <- IO(gainNode.gain.value = musicalEvent.normalizedVelocity / 2)
      _ <- IO(gainNode.connect(audioContext.destination))
      _ <- IO(sourceNode.connect(gainNode))
      _ <- IO {

        if settings.reversed then
          sourceNode.buffer = reverseBuffer(buffer)
        else
          sourceNode.buffer = buffer

        settings.loop match
          case Some(Loop(start, end)) =>
            sourceNode.loop = true
            sourceNode.loopStart = start
            sourceNode.loopEnd = end
          case None =>
            sourceNode.loop = false

        sourceNode.playbackRate.value = settings.playbackRate * playbackRate

        val adjustedDuration = buffer.duration * math.abs(playbackRate)
        sourceNode.start(when, 0, adjustedDuration)
      }
    yield ()
  end playSample
}

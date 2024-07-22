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

object SamplePlayer:

  final case class Settings(
      volume: Double,
      fadeIn: Double,
      fadeOut: Double,
      playbackRate: Double,
      reversed: Boolean,
      loop: Option[Loop],
      startTime: Double,
      offset: Double,
      duration: Option[Double])
  object Settings:
    given Default[Settings] with
      val default: Settings = Settings(1, 0, 0, 1.0, false, None, 0, 0, None)

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

        sourceNode.playbackRate.value = settings.playbackRate * playbackRate

        settings.reversed match
          case true =>
            sourceNode.buffer = reverseBuffer(buffer)
          case false =>
            sourceNode.buffer = buffer

        val startTime = settings.startTime
        val offset = settings.offset

        val duration = settings.duration match
          case Some(d) => d
          case None => (buffer.duration / math.abs(playbackRate)) - offset

        settings.fadeIn match
          case 0 =>
            gainNode.gain.value = settings.volume
          case in if in > 0 =>
            gainNode.gain.value = 0
            gainNode.gain.linearRampToValueAtTime(settings.volume, startTime + offset + in)

        settings.fadeOut match
          case 0 =>
            gainNode.gain.value = settings.volume
          case out if out > 0 =>
            gainNode.gain.linearRampToValueAtTime(
              0,
              startTime + offset + duration - settings.fadeOut)

        settings.loop match
          case Some(Loop(start, end)) =>
            sourceNode.loop = true
            sourceNode.loopStart = start
            sourceNode.loopEnd = end
            sourceNode.start(startTime, start)
          case None =>
            sourceNode.loop = false
            sourceNode.start(startTime, offset, duration)

      }
    yield ()
    end for
  end playSample
end SamplePlayer

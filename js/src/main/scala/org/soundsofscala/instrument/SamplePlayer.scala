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
import org.soundsofscala.models.*
import org.soundsofscala.playback.*

import scala.scalajs.js.JSConverters.iterableOnceConvertible2JSRichIterableOnce
import scala.scalajs.js.typedarray.Float32Array

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
      settings: Settings,
      tempo: Tempo)(using audioContext: AudioContext): IO[Unit] =

//    val startTime = settings.startTime
    val offset = settings.offset
    val duration = musicalEvent.durationToSeconds(tempo)
//    val duration = settings.duration match
//      case Some(d) => d
//      case None => (buffer.duration / math.abs(playbackRate)) - offset

    def createGainNode(volume: Double): IO[dom.GainNode] =
      for
        gainNode <- IO(audioContext.createGain())
        _ <- IO(gainNode.gain.value = volume)
        _ <- IO(gainNode.connect(audioContext.destination))
      yield gainNode

    def createSourceNode(
        buffer: AudioBuffer,
        playbackRate: Double,
        reversed: Boolean): IO[dom.AudioBufferSourceNode] =
      for
        sourceNode <- IO(audioContext.createBufferSource())
        _ <- IO(sourceNode.playbackRate.value = playbackRate)
        _ <- IO(sourceNode.buffer = if reversed then reverseBuffer(buffer) else buffer)
      yield sourceNode

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

    def configureGainNode(when: Double, gainNode: dom.GainNode, settings: Settings): IO[Unit] = IO:
//      val currentTime = audioContext.currentTime
      if settings.fadeIn > 0 then
        gainNode.gain.setValueAtTime(0, when)
        gainNode.gain.linearRampToValueAtTime(settings.volume, when + settings.fadeIn)
      else
        gainNode.gain.setValueAtTime(settings.volume, when)

      if settings.fadeOut > 0 then
        gainNode.gain.setValueAtTime(settings.volume, when + duration - settings.fadeOut)
        gainNode.gain.linearRampToValueAtTime(0, when + (duration - 0.01))
      else
        gainNode.gain.exponentialRampToValueAtTime(0.001, when + duration + 0.4)

    def configureSourceNode(
        when: Double,
        sourceNode: dom.AudioBufferSourceNode,
        settings: Settings): IO[Unit] =
      IO:
        settings.loop match
          case Some(Loop(start, end)) =>
            sourceNode.loop = true
            sourceNode.loopStart = start
            sourceNode.loopEnd = end
            sourceNode.start(when, start, duration)
            sourceNode.stop(when + duration)
          case None =>
            sourceNode.loop = false
            sourceNode.start(when, offset, duration)
            sourceNode.stop(when + (duration + 0.1))
    for
      gainNode <- createGainNode(settings.volume)
      sourceNode <-
        createSourceNode(buffer, settings.playbackRate * playbackRate, settings.reversed)
      _ <- IO(sourceNode.connect(gainNode))
      _ <- configureGainNode(when, gainNode, settings)
      _ <- configureSourceNode(when, sourceNode, settings)
    yield ()
    end for
  end playSample
end SamplePlayer

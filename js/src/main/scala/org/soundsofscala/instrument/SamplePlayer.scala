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
import org.scalajs.dom.{AudioBuffer, AudioContext}
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
      startDelay: Double,
      offset: Double,
      length: Option[Double])

  object Settings:
    given Default[Settings] with
      val default: Settings = Settings(
        volume = 0.5,
        fadeIn = 0,
        fadeOut = 0,
        playbackRate = 1.0,
        reversed = false,
        loop = None,
        startDelay = 0,
        offset = 0,
        length = None
      )

  def playSample(
      buffer: AudioBuffer,
      playbackRate: Double,
      musicalEvent: AtomicMusicalEvent,
      when: Double,
      settings: Settings,
      tempo: Tempo)(using audioContext: AudioContext): IO[Unit] =

    val computedPlaybackRate = playbackRate * settings.playbackRate
    val offset = settings.offset
//    val length = settings.length.getOrElse((buffer.duration / math.abs(playbackRate)) - offset)
    val length = musicalEvent.durationToSeconds(tempo)

    val velocityModulatedVolume = musicalEvent match
      case note: AtomicMusicalEvent.Note =>
        val velocity = note.velocity.getNormalisedVelocity
        settings.volume * velocity
      case _ => settings.volume

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
      if settings.fadeIn > 0 then
        gainNode.gain.setValueAtTime(0, when + settings.startDelay)
        gainNode.gain.linearRampToValueAtTime(
          velocityModulatedVolume,
          when + settings.startDelay + settings.fadeIn)
      else
        gainNode.gain.setValueAtTime(velocityModulatedVolume, when + settings.startDelay)

      if settings.fadeOut > 0 then
        gainNode.gain.setValueAtTime(
          velocityModulatedVolume,
          when + settings.startDelay + length - settings.fadeOut)
        gainNode.gain.linearRampToValueAtTime(0, when + settings.startDelay + length)
      else
        gainNode.gain.setValueAtTime(
          velocityModulatedVolume,
          when + settings.startDelay + length - 0.1)
        gainNode.gain.exponentialRampToValueAtTime(
          0.0001,
          when + settings.startDelay + (length + 0.3))

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
            sourceNode.start(when, start, length)
            sourceNode.stop(when + length)
          case None =>
            sourceNode.loop = false
            sourceNode.start(when + settings.startDelay, offset, length)
            sourceNode.stop(when + settings.startDelay + (length + 0.1))
    for
      gainNode <- createGainNode(velocityModulatedVolume)
      sourceNode <-
        createSourceNode(buffer, computedPlaybackRate, settings.reversed)
      _ <- IO(sourceNode.connect(gainNode))
      _ <- configureGainNode(when, gainNode, settings)
      _ <- configureSourceNode(when, sourceNode, settings)
    yield ()
    end for
  end playSample
end SamplePlayer

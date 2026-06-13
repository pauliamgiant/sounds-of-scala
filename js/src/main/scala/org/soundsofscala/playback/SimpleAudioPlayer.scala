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

package org.soundsofscala.playback

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.syntax.all.*
import org.scalajs.dom.{AudioBuffer, AudioBufferSourceNode, AudioContext, GainNode}
import org.soundsofscala.instrument.SampleLoader
import org.soundsofscala.models.{PauseOffset, StartTime}

final case class SimpleAudioPlayer(path: String)(using audioContext: AudioContext):

  private val audioBuffer: IO[AudioBuffer] = SampleLoader.loadSample(path)

  // scalafix:off DisableSyntax.var
  private var pauseOffset: PauseOffset = PauseOffset(0.0)
  private var sourceNode: Option[AudioBufferSourceNode] = none
  private var gainNode: Option[GainNode] = none
  private var startTime: Option[StartTime] = none
  // scalafix:on

  private val fadeTime = 0.02

  def play(): Unit = playAudio().unsafeRunAndForget()

  private def playAudio(): IO[Unit] =
    for
      _ <- stopPlayback()
      buffer <- audioBuffer
      source = audioContext.createBufferSource()
      gain = audioContext.createGain()
      _ = gain.gain.setValueAtTime(0.0, audioContext.currentTime)
      _ = gain.gain.linearRampToValueAtTime(1.0, audioContext.currentTime + fadeTime)
      _ = source.buffer = buffer
      _ = source.connect(gain)
      _ = gain.connect(audioContext.destination)
      _ = source.start(0, pauseOffset.value)
      _ = startTime = StartTime(audioContext.currentTime - pauseOffset.value).some
      _ = sourceNode = source.some
      _ = gainNode = gain.some
    yield ()

  def stop(): Unit = stopAudio().unsafeRunAndForget()

  private def stopAudio(): IO[Unit] =
    for
      _ <- stopPlayback()
      _ = pauseOffset = PauseOffset(0.0)
      _ = startTime = none
    yield ()

  def pause(): Unit = pauseSong().unsafeRunAndForget()

  private def pauseSong(): IO[Unit] =
    for
      _ <- stopPlayback()
      _ = pauseOffset =
        PauseOffset(audioContext.currentTime - startTime.getOrElse(StartTime(0.0)).value)
      _ <- IO.println(s"pauseOffset is: $pauseOffset")
    yield ()

  private def stopPlayback(): IO[Unit] =
    IO {
      gainNode.foreach { gain =>
        val now = audioContext.currentTime
        gain.gain.setValueAtTime(gain.gain.value, now)
        gain.gain.linearRampToValueAtTime(0.0, now + fadeTime)
      }
      sourceNode.foreach(_.stop(audioContext.currentTime + fadeTime))
      sourceNode = none
      gainNode = none
      startTime = none
    }
end SimpleAudioPlayer

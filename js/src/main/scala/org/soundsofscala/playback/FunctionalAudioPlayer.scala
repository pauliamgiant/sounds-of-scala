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

import cats.effect.{IO, Ref}
import cats.syntax.all.*
import org.scalajs.dom.{AudioBuffer, AudioBufferSourceNode, AudioContext, GainNode}
import org.soundsofscala.instrument.SampleLoader
import org.soundsofscala.models.{FilePath, PauseOffset, StartTime}

case class FunctionalAudioPlayer(
    path: FilePath,
    audioBuffer: AudioBuffer,
    pauseOffset: Ref[IO, PauseOffset],
    sourceNode: Ref[IO, Option[AudioBufferSourceNode]],
    gainNode: Ref[IO, Option[GainNode]],
    startTime: Ref[IO, Option[StartTime]]
)(using audioContext: AudioContext):

  private val fadeTime = 0.02

  def play(): IO[Unit] =
    for
      _ <- stopPlayback()
      source = audioContext.createBufferSource()
      gain = audioContext.createGain()
      offset <- pauseOffset.get
      _ <- IO {
        gain.gain.setValueAtTime(0.0, audioContext.currentTime)
        gain.gain.linearRampToValueAtTime(1.0, audioContext.currentTime + fadeTime)
        source.buffer = audioBuffer
        source.connect(gain)
        gain.connect(audioContext.destination)
        source.start(0, offset.value)
      }
      _ <- startTime.set(StartTime(audioContext.currentTime - offset.value).some)
      _ <- sourceNode.set(source.some)
      _ <- gainNode.set(gain.some)
    yield ()

  def stop(): IO[Unit] =
    for
      _ <- stopPlayback()
      _ <- pauseOffset.set(PauseOffset(0.0))
      _ <- startTime.set(none)
    yield ()

  def pause(): IO[Unit] =
    for
      _ <- stopPlayback()
      startOpt <- startTime.get
      offset = audioContext.currentTime - startOpt.getOrElse(StartTime(0.0)).value
      _ <- pauseOffset.set(PauseOffset(offset))
      _ <- IO.println(s"Pause offset is: $offset")
    yield ()

  private def stopPlayback(): IO[Unit] =
    for
      sourceOpt <- sourceNode.get
      gainOpt <- gainNode.get
      _ <- IO.println(
        s"Calling stopPlayback. Source node is ${
            if sourceOpt.isDefined then "" else "not"
          } defined.")
      _ <- gainOpt.traverse { gain =>
        val now = audioContext.currentTime
        IO {
          gain.gain.setValueAtTime(gain.gain.value, now)
          gain.gain.linearRampToValueAtTime(0.0, now + fadeTime)
        }
      }
      _ <- sourceOpt.traverse(source => IO(source.stop(audioContext.currentTime + fadeTime)))
      _ <- sourceNode.set(none)
      _ <- gainNode.set(none)
      _ <- startTime.set(none)
    yield ()
end FunctionalAudioPlayer

object AudioPlayer:

  def apply(audioFilePath: FilePath)(using audioContext: AudioContext): IO[FunctionalAudioPlayer] =
    for
      buffer <- SampleLoader.loadSample(audioFilePath.value)
      pauseOffset <- Ref.of[IO, PauseOffset](PauseOffset(0.0))
      sourceNode <- Ref.of[IO, Option[AudioBufferSourceNode]](none)
      gainNode <- Ref.of[IO, Option[GainNode]](none)
      startTime <- Ref.of[IO, Option[StartTime]](none)
    yield FunctionalAudioPlayer(
      audioFilePath,
      buffer,
      pauseOffset,
      sourceNode,
      gainNode,
      startTime
    )

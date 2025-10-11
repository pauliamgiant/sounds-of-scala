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

package org.soundsofscala.models

import cats.data.NonEmptyList
import cats.effect.{IO, Ref}
import cats.effect.kernel.Fiber
import org.scalajs.dom.AudioContext
import org.soundsofscala.transport.Sequencer

object Song:
  def apply(
      title: Title,
      tempo: Tempo = Tempo(120),
      swing: Swing = Swing(0),
      mixer: Mixer): IO[Song] =
    for
      runningSequencerRef <- Ref.of[IO, Option[Fiber[IO, Throwable, Unit]]](None)
    yield new Song(title, tempo, swing, mixer, runningSequencerRef)

final class Song private (
    val title: Title,
    val tempo: Tempo,
    val swing: Swing,
    val mixer: Mixer,
    private val runningSequencerRef: Ref[IO, Option[Fiber[IO, Throwable, Unit]]]
):

  def play()(using AudioContext): IO[Unit] =
    for
      _ <- IO.println(s"Playing: $title")
      _ <- stop() // Stop any currently running sequencer first
      fiber <- Sequencer().playSong(this).start
      _ <- runningSequencerRef.set(Some(fiber))
    yield ()

  def stop(): IO[Unit] =
    for
      _ <- IO.println(s"Song.stop() called for: $title")
      currentSequencer <- runningSequencerRef.get
      _ <- currentSequencer match
        case Some(fiber) =>
          IO.println("Cancelling running sequencer") *>
            fiber.cancel *>
            runningSequencerRef.set(None)
        case None =>
          IO.println("No running sequencer to cancel")
      _ <- Sequencer().stopSong(this) // Stop current oscillators
    yield ()
end Song

case class Mixer(tracks: NonEmptyList[Track[?]])
object Mixer:
  def apply(tracks: Track[?]*): Mixer = Mixer(
    NonEmptyList(tracks.head, tracks.tail.toList)
  )

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
import cats.effect.IO
import org.scalajs.dom.AudioContext
import org.soundsofscala.transport.Sequencer

case class Song(
    title: Title,
    tempo: Tempo = Tempo(120),
    swing: Swing = Swing(0),
    mixer: Mixer
):
  def play()(using AudioContext): IO[Unit] =
    IO.println(s"Playing: $title") *> Sequencer().playSong(this)

case class Mixer(tracks: NonEmptyList[Track])
object Mixer:
  def apply(tracks: Track*): Mixer = Mixer(
    NonEmptyList(tracks.head, tracks.tail.toList)
  )

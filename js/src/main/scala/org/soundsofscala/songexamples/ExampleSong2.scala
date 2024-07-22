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

package org.soundsofscala.songexamples

import cats.effect.IO
import org.scalajs.dom.AudioContext
import org.soundsofscala.instrument.{SamplePlayer, Sampler}
import org.soundsofscala.models.*
import org.soundsofscala.playback.*
import org.soundsofscala.syntax.all.*

object ExampleSong2:
  val musicalEvent: MusicalEvent =
    D2

  val customSettings: SamplePlayer.Settings =
    SamplePlayer.Settings(
      playbackRate = 1,
      reversed = false,
      loop = Some(Loop(start = 2, end = 6)),
      attack = Attack(0),
      release = Release(0.9),
      startTime = 1,
      offset = 1,
      duration = Some(1)
    )

  def play(): AudioContext ?=> IO[Unit] =
    for {
      piano <- Sampler.rhubarb
      song = Song(
        title = Title("Rhubarb Loop"),
        tempo = Tempo(110),
        swing = Swing(0),
        mixer = Mixer(
          Track(
            Title("rhubarb D3"),
            musicalEvent,
            piano,
            customSettings = Some(customSettings))
        )
      )
      a <- song.play()
    } yield a
end ExampleSong2

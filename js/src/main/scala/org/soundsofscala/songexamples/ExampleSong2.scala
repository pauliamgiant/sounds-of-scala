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
import org.soundsofscala.instrument.SamplePlayer
import org.soundsofscala.instrument.Sampler
import org.soundsofscala.models.*
//import org.soundsofscala.playback.*
import org.soundsofscala.syntax.all.*

object ExampleSong2:
  val musicalEvent: MusicalEvent =
    D2 + A2 + F2 + A3 + D4 + F2 + D5 + D2

  val customSettings: SamplePlayer.Settings =
    SamplePlayer.Settings(
      volume = 1,
      playbackRate = 1,
      reversed = false,
      loop = None, // Some(Loop(start = 1, end = 1.5)),
      fadeIn = 0,
      fadeOut = 0.5,
      startTime = 0,
      offset = 0,
      duration = Some(1)
    )

  def play(): AudioContext ?=> IO[Unit] =
    for
      piano <- Sampler.piano
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
    yield a
end ExampleSong2

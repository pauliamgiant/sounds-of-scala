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
import org.soundsofscala.syntax.all.*

object ExampleSong3:

  val customSettings: SamplePlayer.Settings =
    SamplePlayer.Settings(
      volume = 1,
      playbackRate = 1,
      reversed = true,
      loop = None, // Some(Loop(start = 2, end = 6)),
      fadeIn = 0,
      fadeOut = 0,
      startDelay = 0,
      offset = 0,
      length = Some(1)
    )

  val musicalEvent: MusicalEvent =
    Dmin7 + Amin7 + Dmin7 + Amin7 + Dmin7 + Amin7 + Dmin7 + Amin7

  def play(): AudioContext ?=> IO[Unit] =
    for
      piano <- Sampler.guitar
      song = Song(
        title = Title("Dissonant Twinkle Twinkle"),
        tempo = Tempo(110),
        swing = Swing(0),
        mixer = Mixer(
          Track(Title("Single Synth Voice"), musicalEvent, piano)
        )
      )
      a <- song.play()
    yield a
end ExampleSong3

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
import org.soundsofscala.instrument.PianoSampler
import org.soundsofscala.models.*
import org.soundsofscala.syntax.all.*

object ExampleSong0:

  val musicalEvent: MusicalEvent =
    C3 + C3 + G3 + G3 + A3 + A3 + G3.half |
      F3 + F3 + E3 + E3 + D3 + D3 + C3.half

  def play(): AudioContext ?=> IO[Unit] =
    for {
      piano <- PianoSampler.default
      song = Song(
        title = Title("Something We All Know"),
        tempo = Tempo(110),
        swing = Swing(0),
        mixer = Mixer(
          Track(Title("Single Synth Voice"), musicalEvent, piano)
        )
      )
      a <- song.play()
    } yield a
end ExampleSong0

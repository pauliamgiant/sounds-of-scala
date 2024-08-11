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
      reversed = false,
      loop = None, // Some(Loop(start = 2, end = 6)),
      fadeIn = 0,
      fadeOut = 0,
      startTime = 0,
      offset = 0,
      duration = Some(1)
    )

  val musicalEvent: MusicalEvent =
    `C-2`.sixteenth + `C-1`.sixteenth + C0.sixteenth + C1.sixteenth |
      C2.sixteenth + C3.sixteenth + C4.sixteenth + C5.sixteenth |
      C6.sixteenth + C7.sixteenth + C8.sixteenth |
      C4.flat + C4 + G4.sharp + G4 + A4.sharp + A4 + G4.half |
      F3.flat + F3 + E3.sharp + E3 + D3.flat + D3 + C3.half

  def play(): AudioContext ?=> IO[Unit] =
    for
      piano <- Sampler.piano
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

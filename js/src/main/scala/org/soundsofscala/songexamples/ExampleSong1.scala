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
import org.soundsofscala.instrument.Sampler
import org.soundsofscala.models.*
import org.soundsofscala.syntax.all.*

object ExampleSong1:

  val vinylHihat: MusicalEvent =
    RestEighth + C4.eighth + RestEighth + C4.eighth + RestQuarter + RestSixteenth + C4.sixteenth + RestEighth

  val rhubarbHigh: MusicalEvent =
    C2 + TwoBarRest + TwoBarRest

  val rhubarbLow: MusicalEvent =
    C1 + TwoBarRest + TwoBarRest

  val vinylTrack: MusicalEvent =
    OneBarRest + C1 + TwoBarRest + C1 + TwoBarRest + C1 + TwoBarRest + C1 + TwoBarRest

  val sparklesTrack: MusicalEvent =
    TwoBarRest + TwoBarRest + C3 + TwoBarRest + TwoBarRest

  val kickTrack: MusicalEvent =
    C2.eighth + C2.eighth + RestQuarter + OneBarRest + RestHalf + C2.eighth + RestEighth + RestQuarter + OneBarRest + RestHalf

  val snareTrack: MusicalEvent =
    (RestQuarter + C2.eighth + RestEighth).repeat(
      7) + RestSixteenth + C2.sixteenth + RestEighth + C2.eighth + RestEighth

  val rimTrack: MusicalEvent =
    RestHalf + RestSixteenth + C3.eighth + RestSixteenth + RestQuarter + RestHalf + RestSixteenth + C3.eighth + RestEighth + C3.eighth + RestSixteenth + RestHalf + RestSixteenth + C3.eighth + RestSixteenth + RestQuarter + OneBarRest

  def play(): AudioContext ?=> IO[Unit] =
    for {
      rhubarb <- Sampler.rhubarb
      vinyl <- Sampler.vinyl
      sparkles <- Sampler.sparkles
      kick <- Sampler.kickDrum
      snare <- Sampler.snareDrum
      rim <- Sampler.rimShot
      song = Song(
        title = Title("Rhubarb"),
        tempo = Tempo(110),
        swing = Swing(0),
        mixer = Mixer(
          Track(Title("RhubarbHigh"), rhubarbHigh.loop, rhubarb),
          Track(Title("RhubarbLow"), rhubarbLow.loop, rhubarb),
          Track(Title("Vinyl"), vinylTrack.loop, vinyl),
          Track(Title("Sparkles"), sparklesTrack.loop, sparkles),
          Track(Title("Kick"), kickTrack.loop, kick),
          Track(Title("Snare"), snareTrack.loop, snare),
          Track(Title("VinylHihat"), vinylHihat.loop, vinyl)
          // Track(Title("Rim"), rimTrack.loop, rim)
        )
      )
      a <- song.play()
    } yield a
end ExampleSong1

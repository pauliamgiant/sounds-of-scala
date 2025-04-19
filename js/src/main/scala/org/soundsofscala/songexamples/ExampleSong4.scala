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
import org.soundsofscala.instrument.{ScalaSynth, Simple80sDrumMachine}
import org.soundsofscala.models.*
import org.soundsofscala.syntax.all.*

object ExampleSong4:

  val kd = (KickDrum + RestQuarter).loop
  val sd = (RestQuarter + SnareDrum).loop
  val ht = HatsClosed.eighth.loop

  val musicalEvent: MusicalEvent =
    C1.wholeDotted.softest + D1.wholeDotted.softest + E1.wholeDotted.softest + B0.wholeDotted.softest

  def play(): AudioContext ?=> IO[Unit] =

    val song = Song(
      title = Title("long note"),
      tempo = Tempo(110),
      swing = Swing(0),
      mixer = Mixer(
        Track(Title("Single Synth Voice"), musicalEvent.loop, ScalaSynth()),
        Track(Title("Kick"), FourBarRest + kd, Simple80sDrumMachine()),
        Track(Title("Snare"), FourBarRest + sd, Simple80sDrumMachine()),
        Track(Title("Hats"), FourBarRest + ht, Simple80sDrumMachine())
      )
    )
    song.play()
end ExampleSong4

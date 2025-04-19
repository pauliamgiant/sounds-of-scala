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
import org.soundsofscala.instrument.*
import org.soundsofscala.models.*
import org.soundsofscala.syntax.all.*

object ExampleSong4:

  val kd = (KickDrum + RestQuarter).loop
  val sd = (RestQuarter + SnareDrum).loop
  val ht = HatsClosed.eighth.loop

  val musicalEvent: MusicalEvent =
    C1.wholeDotted.onFull + D1.wholeDotted.onFull + E1.wholeDotted.onFull + B0.wholeDotted.onFull

  def play(): AudioContext ?=> IO[Unit] =
    for
      liveBass <- Sampler.bassGuitar
      song = Song(
        title = Title("long note"),
        tempo = Tempo(110),
        swing = Swing(0),
        mixer = Mixer(
          Track(Title("Live Bass"), musicalEvent.loop, liveBass),
          Track(Title("Kick"), FourBarRest + kd, Simple80sDrumMachine()),
          Track(Title("Snare"), FourBarRest + sd, Simple80sDrumMachine()),
          Track(Title("Hats"), FourBarRest + ht, Simple80sDrumMachine())
        )
      )
      songUnit <- song.play()
    yield songUnit

end ExampleSong4

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
import org.soundsofscala
import org.soundsofscala.instrument.Simple80sDrumMachine
import org.soundsofscala.models.*
import org.soundsofscala.syntax.all.*

object ExampleDrumBeat1:

  val kick: MusicalEvent = kk + (r8triplet + r8triplet + kk.eighthTriplet) + kk + r4
  val snare: MusicalEvent = (r4 + sn).repeat
  val hats: MusicalEvent =
    (hhc.eighthTriplet + hhc.eighthTriplet.p + hhc.eighthTriplet.mp) * 4

  def play(): AudioContext ?=> IO[Unit] =
    for {
      drumMachine <- IO(Simple80sDrumMachine())
      song = Song(
        title = Title("Drum Synth Song"),
        tempo = Tempo(90),
        swing = Swing(0),
        mixer = Mixer(
          Track(Title("Kick"), kick.repeat(12), drumMachine),
          Track(Title("Snare"), snare.repeat(12), drumMachine),
          Track(Title("HiHats"), hats.repeat(12), drumMachine)
        )
      )
      _ <- song.play()
    } yield ()

end ExampleDrumBeat1

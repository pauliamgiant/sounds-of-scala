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

object ExampleSong6:

  private val triplet5DGATrebleClef = D5.sharp.eighthTriplet + G5.sharp.eighthTriplet + A5.sharp
  private val triplet5BAGTrebleClef = B5.sharp.eighthTriplet + A5.sharp.eighthTriplet + G5.sharp
  private val triplet655CAGTrebleClef = C6.sharp.eighthTriplet + A5.sharp.eighthTriplet + G5.sharp
  private val triplet655EABTrebleClef = E6.sharp.eighthTriplet + A5.sharp.eighthTriplet + B5.sharp

  private val measureOneTrebleClef =
    triplet5DGATrebleClef + triplet5BAGTrebleClef + triplet5DGATrebleClef
  private val measureTwoTrebleClef =
    triplet5BAGTrebleClef + triplet5DGATrebleClef + triplet5BAGTrebleClef
  private val measureThreeTrebleClef =
    triplet5DGATrebleClef + triplet655CAGTrebleClef + triplet655EABTrebleClef
  private val measureFourTrebleClef =
    triplet655CAGTrebleClef + triplet5DGATrebleClef + triplet5BAGTrebleClef

  def song(): AudioContext ?=> IO[Song] =
    for
      violinSynth <- ViolinSynth()
    yield Song(
      title = Title("Laideronnette, impératrice des pagodes"),
      tempo = Tempo(110),
      mixer = Mixer(
        Track(
          Title("Laideronnette, impératrice des pagodes"),
          measureOneTrebleClef + measureTwoTrebleClef + measureThreeTrebleClef +
            measureFourTrebleClef,
          violinSynth,
          Playback.OneShot
        )
      )
    )
end ExampleSong6

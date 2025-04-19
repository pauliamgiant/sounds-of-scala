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

object ExampleSong1:

  private val synthSettings: Synth.Settings =
    Synth.Settings(
      attack = Attack(0.1),
      release = Release(0.1),
      pan = 0.5
    )

  val anotherOneBitesTheRust: MusicalEvent =
    E1.eighth +
      RestEighth +
      E1.eighth +
      RestEighth +
      E1.eighth +
      RestEighth +
      RestEighth.eighthDotted +
      E1.sixteenth +
      (E1.eighth.repeat(2)) +
      G1.eighth +
      E1.sixteenth +
      A1.quarter +
      RestEighth.eighthDotted +
      A1.sixteenth +
      G1.sixteenth

  private val kd = (KickDrum + RestQuarter).repeat(8)
  private val sd = (RestQuarter + SnareDrum).repeat(8)
  private val ht = HatsClosed.eighth.repeat(32)

  def play(): AudioContext ?=> IO[Unit] =
    Song(
      title = Title("Something We All Know"),
      tempo = Tempo(110),
      swing = Swing(0),
      mixer = Mixer(
        Track(Title("Kick"), kd, Simple80sDrumMachine()),
        Track(Title("Snare"), sd, Simple80sDrumMachine()),
        Track(Title("Hats"), ht, Simple80sDrumMachine()),
        Track(
          Title("Single Synth Voice"),
          anotherOneBitesTheRust.repeat(2),
          ScalaSynth(),
          customSettings = Some(synthSettings))
      )
    ).play()
end ExampleSong1

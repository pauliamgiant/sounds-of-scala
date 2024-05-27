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
import org.soundsofscala.instrument.*
import org.soundsofscala.models.*
import org.soundsofscala.syntax.all.*

object ExampleSong3Chords:

  val bassLine: MusicalEvent =
    val bar1 =
      G1.medium + RestEighth + RestSixteenth + G1.medium.sixteenth + G1.medium + RestQuarter
    val bar2 = bar1
    val bar3 =
      A1.medium + RestEighth + RestSixteenth + A1.medium.sixteenth + A(
        Octave(1)).medium + G1.medium
    val bar4 =
      F1.medium + RestEighth + RestSixteenth + F1.medium.sixteenth + F1.medium.half
    (bar1 + bar2 + bar3 + bar4).repeat(4)

  val pianoPart: MusicalEvent = (Chord(G3.softest, B3.medium, E3.assertively)) * 4

  val kickSequence: MusicalEvent = kk + (r8triplet + r8triplet + kk.eighthTriplet) + kk + r4
  val snareSequence: MusicalEvent = (RestQuarter + SnareDrum).repeat
  val hatsSequence: MusicalEvent =
    (hhc.eighthTriplet.onFull + hhc.eighthTriplet.soft + hhc.eighthTriplet.soft) * 4

  val clapSequence: MusicalEvent = (RestQuarter + RestQuarter + RestQuarter + HandClap).repeat

  def play(): AudioContext ?=> IO[Unit] =
    for {
      piano <- PianoSampler.default
      song = Song(
        title = Title("Chords of Joy"),
        tempo = Tempo(92),
        swing = Swing(0),
        mixer = Mixer(
          Track(Title("Kick"), FourBarRest + kickSequence.repeat(12), Simple80sDrumMachine()),
          Track(Title("Snare"), FourBarRest + snareSequence.repeat(12), Simple80sDrumMachine()),
          Track(Title("HiHats"), hatsSequence.repeat(12), Simple80sDrumMachine()),
          Track(Title("Clap"), clapSequence.repeat(12), Simple80sDrumMachine()),
          Track(Title("Bass"), FourBarRest + bassLine, ScalaSynth()),
          Track(Title("Piano"), pianoPart, piano)
        )
      )
      a <- song.play()
    } yield a
end ExampleSong3Chords

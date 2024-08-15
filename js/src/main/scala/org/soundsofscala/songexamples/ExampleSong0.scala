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

object ExampleSong0:

  private val synthSettings: Synth.Settings =
    Synth.Settings(
      attack = Attack(0.1),
      release = Release(0.1)
    )

  private val samplerSettings: SamplePlayer.Settings =
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
//    A2.soft + A2.soft + A2.soft + A2.soft |
//      G2.soft + G2.soft + G2.soft + G2.soft |
//      F2.soft + F2.soft + F2.soft + F2.soft |
//      G2.soft + G2.soft + G2.soft + G2.soft

  val kd = (C2 + RestQuarter).repeat(8)
  val sd = (RestQuarter + D2).repeat(8)
  val ht = E2.eighth.repeat(32)

  private def drumSampler(): AudioContext ?=> IO[Sampler] = Sampler.fromPaths(
    List(
      SampleKey(Pitch.C, Accidental.Natural, Octave(2)) -> "resources/audio/drums/NeonKick.wav",
      SampleKey(Pitch.D, Accidental.Natural, Octave(2)) -> "resources/audio/drums/NeonSnare.wav",
      SampleKey(Pitch.E, Accidental.Natural, Octave(2)) -> "resources/audio/drums-808/Hats808.wav"
    )
  )

  def play(): AudioContext ?=> IO[Unit] =
    for
      piano <- Sampler.piano
      drums <- drumSampler()
      song = Song(
        title = Title("Something We All Know"),
        tempo = Tempo(110),
        swing = Swing(0),
        mixer = Mixer(
          Track(Title("Kick"), kd, drums, customSettings = Some(samplerSettings)),
          Track(Title("Snare"), sd, drums, customSettings = Some(samplerSettings)),
          Track(Title("Hats"), ht, drums, customSettings = Some(samplerSettings)),
          Track(
            Title("Single Synth Voice"),
            anotherOneBitesTheRust.repeat(2),
            ScalaSynth(),
            customSettings = Some(synthSettings))
//          Track(
//            Title("Single Piano Voice"),
//            anotherOneBitesTheRust.repeat(2),
//            piano,
//            customSettings = Some(samplerSettings))
        )
      )
      a <- song.play()
    yield a
end ExampleSong0

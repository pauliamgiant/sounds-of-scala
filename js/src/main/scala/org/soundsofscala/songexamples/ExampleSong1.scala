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
      volume = 0.6,
      attack = Attack(0.1),
      release = Release(0.1),
      pan = 0.0
    )

  private val samplerSettings: SamplePlayer.Settings =
    SamplePlayer.Settings(
      volume = 1,
      playbackRate = 1,
      reversed = false,
      loop = None, // Some(Loop(start = 2, end = 6)),
      fadeIn = 0,
      fadeOut = 1,
      startDelay = 0,
      offset = 0,
      length = Some(200)
    )

  val anotherOneBitesTheRust: MusicalEvent =
    E1.eighth.soft +
      RestEighth +
      E1.eighth.soft +
      RestEighth +
      E1.eighth.soft +
      RestEighth +
      RestEighth.eighthDotted +
      E1.sixteenth.soft +
      (E1.eighth.soft.repeat(2)) +
      G1.eighth.soft +
      E1.sixteenth.soft +
      A1.quarter.softest +
      RestEighth.eighthDotted +
      A1.sixteenth.soft +
      G1.sixteenth.soft

  val kd = (C2 + RestQuarter.onFull).repeat(16)
  val sd = (RestQuarter + D2).repeat(16)
  val ht = (E2.eighth.medium * 4).repeat(16)

  private def drumSampler(): AudioContext ?=> IO[Sampler] = Sampler.fromPaths(
    List(
      SampleKey(Pitch.C, Accidental.Natural, Octave(2)) -> "resources/audio/drums/KickDrum.wav",
      SampleKey(Pitch.D, Accidental.Natural, Octave(2)) -> "resources/audio/drums/SnareDrum.wav",
      SampleKey(
        Pitch.E,
        Accidental.Natural,
        Octave(2)) -> "resources/audio/drums-electro/HatsVintageElectro.wav"
    )
  )

  def play(): AudioContext ?=> IO[Unit] =
    for
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
            TwoBarRest + anotherOneBitesTheRust.repeat(2),
            ScalaSynth(),
            customSettings = Some(synthSettings))
        )
      )
      a <- song.play()
    yield a
end ExampleSong1

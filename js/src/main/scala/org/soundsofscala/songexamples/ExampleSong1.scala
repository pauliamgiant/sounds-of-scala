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
import cats.syntax.all.*
import org.scalajs.dom.AudioContext
import org.soundsofscala.instrument.*
import org.soundsofscala.models.*
import org.soundsofscala.syntax.all.*

object ExampleSong1:

  private val guitarSamplerSettings: SamplePlayer.Settings =
    SamplePlayer.Settings(
      volume = 0.2,
      playbackRate = 1,
      reversed = false,
      loop = none, // Some(Loop(start = 2, end = 6)),
      fadeIn = 0,
      fadeOut = 0,
      startDelay = 0,
      offset = 0,
      length = none
    )

  private val drumSamplerSettings: SamplePlayer.Settings =
    SamplePlayer.Settings(
      volume = 0.8,
      playbackRate = 1,
      reversed = false,
      loop = none, // Some(Loop(start = 2, end = 6)),
      fadeIn = 0,
      fadeOut = 0,
      startDelay = 0,
      offset = 0,
      length = none
    )

  private val bassline: MusicalEvent =
    D1.eighth + C1.eighth + A0.eighth + A0.eighth + C1.eighth + C1.eighth + A0.eighth + C1.eighth

  private val guitarPart: MusicalEvent =
    (Chord(D3, F3, A2).quarterDotted + Chord(C3, E3, G3).half + RestEighth).repeat(4)

  private val verseMelody: MusicalEvent =
    RestQuarter.quarterDotted + A4.eighth + D5.eighth + E5.quarter + F5.quarterDotted +
      E5.eighth + RestEighth + D5.eighth + C5.eighth + D5.quarter + D5.quarter + RestQuarter + RestHalf + OneBarRest

  val kd = (C2 + RestQuarter.onFull).loop
  val sd = (RestQuarter + D2).loop
  val ht = (E2.eighth.medium * 4).loop

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
      bassGuitar <- Sampler.bassGuitar
      guitar <- Sampler.guitar
      song = Song(
        title = Title("Song Example 1"),
        tempo = Tempo(110),
        swing = Swing(0),
        mixer = Mixer(
          Track(Title("Kick"), kd, drums, customSettings = Some(drumSamplerSettings)),
          Track(Title("Snare"), sd, drums, customSettings = Some(drumSamplerSettings)),
          Track(Title("Hats"), ht, drums, customSettings = Some(drumSamplerSettings)),
          Track(
            Title("Live Bass Guitar"),
            TwoBarRest + bassline.repeat(12),
            bassGuitar
          ),
          Track(
            Title("Live Guitar"),
            FourBarRest + TwoBarRest + guitarPart,
            guitar,
            customSettings = guitarSamplerSettings.some
          ),
          Track(
            Title("Lead Line"),
            FourBarRest + verseMelody.repeat(4),
            PianoSynth()
          )
        )
      )
      a <- song.play()
    yield a
end ExampleSong1

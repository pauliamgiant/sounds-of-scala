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
import org.soundsofscala.models.Playback
import org.soundsofscala.syntax.all.*

object ExampleSong7SwingIt:

  private val drumSamplerSettings: SamplePlayer.Settings =
    SamplePlayer.Settings(
      volume = 0.8,
      playbackRate = 1,
      reversed = false,
      loop = none,
      fadeIn = 0,
      fadeOut = 0,
      startDelay = 0,
      offset = 0,
      length = none
    )

  val kickDrum: MusicalEvent = (C2 + RestQuarter.onFull).repeat(32)
  val snareDrum: MusicalEvent = (RestQuarter + D2).repeat(32)
  val hiHats: MusicalEvent = (E2.eighth.medium * 8).repeat(32)

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

  def song(): AudioContext ?=> IO[Song] =
    for
      drums <- drumSampler()
    yield Song(
      title = Title("Song Example 7 with Swing"),
      tempo = Tempo(120),
      swing = Swing(SwingAmount(5), SwingResolution.Eighth),
      mixer = Mixer(
        Track(
          Title("Kick"),
          kickDrum,
          drums,
          Playback.Loop,
          customSettings = drumSamplerSettings.some),
        Track(
          Title("Snare"),
          snareDrum,
          drums,
          Playback.Loop,
          customSettings = drumSamplerSettings.some),
        Track(
          Title("Hats"),
          hiHats,
          drums,
          Playback.Loop,
          customSettings = drumSamplerSettings.some)
      )
    )
end ExampleSong7SwingIt

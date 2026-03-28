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
import org.soundsofscala.instrument.{SamplePlayer, Sampler}
import org.soundsofscala.models.*
import org.soundsofscala.syntax.all.*

object ExampleSongSampler:

  val vinylHihat: MusicalEvent =
    RestEighth + C4.eighth + RestEighth + C4.eighth + RestQuarter + RestSixteenth + C4.sixteenth + RestEighth

  val rhubarbHigh: MusicalEvent =
    C2 + TwoBarRest + TwoBarRest

  val rhubarbLow: MusicalEvent =
    C1 + TwoBarRest + TwoBarRest

  val vinylTrack: MusicalEvent =
    OneBarRest + C1 + TwoBarRest + C1 + TwoBarRest + C1 + TwoBarRest + C1 + TwoBarRest

  val sparklesTrack: MusicalEvent =
    TwoBarRest + TwoBarRest + C3 + TwoBarRest + TwoBarRest

  val sparklesTrackReversed: MusicalEvent =
    C3 + TwoBarRest + C3 + TwoBarRest

  val kickTrack: MusicalEvent =
    C2.eighth + C2.eighth + RestQuarter + OneBarRest + RestHalf + C2.eighth + RestEighth + RestQuarter + OneBarRest + RestHalf

  val snareTrack: MusicalEvent =
    (RestQuarter + C2.eighth + RestEighth).repeat(
      7) + RestSixteenth + C2.sixteenth + RestEighth + C2.eighth + RestEighth

  val customSettings: SamplePlayer.Settings =
    SamplePlayer.Settings(
      volume = 1,
      playbackRate = 1,
      reversed = true,
      loop = None, // Some(Loop(start = 1, end = 1.5)),
      fadeIn = 0.2,
      fadeOut = 0,
      startDelay = 0,
      offset = 2,
      length = Some(1)
    )

  def song(): AudioContext ?=> IO[Song] =
    for
      rhubarb <- Sampler.rhubarb
      vinyl <- Sampler.vinyl
      sparkles <- Sampler.sparkles
      kick <- Sampler.kickDrum
      snare <- Sampler.snareDrum
    yield Song(
      title = Title("Rhubarb"),
      tempo = Tempo(110),
      mixer = Mixer(
        Track(Title("RhubarbHigh"), rhubarbHigh, rhubarb, Playback.OneShot),
        Track(Title("RhubarbLow"), rhubarbLow, rhubarb, Playback.OneShot),
        Track(Title("Vinyl"), vinylTrack, vinyl, Playback.OneShot),
        Track(Title("Sparkles"), sparklesTrack, sparkles, Playback.OneShot),
        Track(
          Title("SparklesReversed"),
          sparklesTrackReversed,
          sparkles,
          Playback.OneShot,
          customSettings = customSettings.some),
        Track(Title("Kick"), kickTrack, kick, Playback.OneShot),
        Track(Title("Snare"), snareTrack, snare, Playback.OneShot),
        Track(Title("VinylHihat"), vinylHihat, vinyl, Playback.OneShot)
      )
    )
end ExampleSongSampler

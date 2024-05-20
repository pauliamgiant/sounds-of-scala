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

import org.scalajs.dom.AudioContext
import org.soundsofscala
import org.soundsofscala.Instruments.SimpleDrums
import org.soundsofscala.models.*
import org.soundsofscala.syntax.all.*

object PolyRhythmicDrums extends SongExample:

  val simple: MusicalEvent = RestQuarter + SnareDrum + RestQuarter + SnareDrum

  val kick7Eight: MusicalEvent = kk + r8 + r16 + kk.sixteenth + kk + r8
  val snare: MusicalEvent = (r4 + sn).repeat
  val hats5Eight: MusicalEvent =
    (hhc.sixteenth + hhc.sixteenth.softest + hhc.eighth + hhc.eighth + hhc.sixteenth + hhc
      .sixteenth
      .softest + hhc.eighth).repeat(4)

  override def song(): AudioContext ?=> Song =
    Song(
      title = Title("PolyRhythmic Drums"),
      tempo = Tempo(112),
      swing = Swing(0),
      mixer = Mixer(
        Track(Title("Kick"), kick7Eight.repeat(12), SimpleDrums()),
        Track(Title("Snare"), snare.repeat(12), SimpleDrums()),
        Track(Title("HiHats"), hats5Eight.repeat(12), SimpleDrums())
      )
    )

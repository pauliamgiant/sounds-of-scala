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

package org.soundsofscala

import cats.effect.IO
import org.soundsofscala.models.Accidental.*
import org.soundsofscala.models.DrumVoice.*
import org.soundsofscala.models.Duration.*
import weaver.SimpleIOSuite

object TransformMusicalEventsTest extends SimpleIOSuite:

  test("drumVoiceToString maps all drum voices"):
    val mappings = List(
      (Kick, "Doob"),
      (Snare, "Crack"),
      (HiHatClosed, "Tsst"),
      (HiHatOpen, "Tssssss"),
      (Crash, "Pshhhh"),
      (Ride, "Ting"),
      (TomHigh, "Dim"),
      (TomMid, "Dum"),
      (FloorTom, "Duh"),
      (Rimshot, "Tock"),
      (Clap, "Clap"),
      (Cowbell, "Ding"),
      (Tambourine, "Chinka")
    )
    IO.pure(forEach(mappings): (drum, expected) =>
      expect.same(TransformMusicalEvents.drumVoiceToString(drum), expected))

  test("durationToString produces correct lengths"):
    val mappings = List(
      (Whole, 64 * 3),
      (Half, 32 * 3),
      (Quarter, 16 * 3),
      (Eighth, 8 * 3),
      (Sixteenth, 4 * 3),
      (ThirtySecond, 2 * 3),
      (SixtyFourth, 1 * 3),
      (HalfTriplet, 64),
      (QuarterTriplet, 32),
      (EighthTriplet, 16),
      (SixteenthTriplet, 8),
      (ThirtySecondTriplet, 4)
    )
    IO.pure(forEach(mappings): (duration, expected) =>
      expect(TransformMusicalEvents.durationToString(duration, "").length == expected))

  test("durationToString produces correct lengths for dotted durations"):
    val mappings = List(
      (WholeDotted, (64 * 3 * 1.5).toInt),
      (HalfDotted, (32 * 3 * 1.5).toInt),
      (QuarterDotted, (16 * 3 * 1.5).toInt),
      (EighthDotted, (8 * 3 * 1.5).toInt),
      (SixteenthDotted, (4 * 3 * 1.5).toInt),
      (ThirtySecondDotted, (2 * 3 * 1.5).toInt)
    )
    IO.pure(forEach(mappings): (duration, expected) =>
      expect(TransformMusicalEvents.durationToString(duration, "").length == expected))

  test("accidentalToString maps all accidentals"):
    val mappings = List(
      (Sharp, "#"),
      (Flat, "♭"),
      (Natural, "")
    )
    IO.pure(forEach(mappings): (accidental, expected) =>
      expect.same(TransformMusicalEvents.accidentalToString(accidental), expected))
end TransformMusicalEventsTest

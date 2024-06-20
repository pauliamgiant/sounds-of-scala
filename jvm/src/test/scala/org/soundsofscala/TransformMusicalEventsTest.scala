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

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.soundsofscala.models.Accidental.*
import org.soundsofscala.models.DrumVoice.*
import org.soundsofscala.models.Duration.*

class TransformMusicalEventsTest extends AnyFunSuite with Matchers with TableDrivenPropertyChecks:

  test("testDrumVoiceToString"):
    Table(
      ("drumVoice", "expected"),
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
    ).forEvery: (drumVoice, expected) =>
      TransformMusicalEvents.drumVoiceToString(drumVoice) shouldBe expected

  test("testDurationToString"):
    Table(
      ("duration", "expected"),
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
    ).forEvery: (duration, expected) =>
      TransformMusicalEvents.durationToString(duration, "").length shouldBe expected

  test("testAccidentalToString"):
    Table(
      ("accidental", "expected"),
      (Sharp, "#"),
      (Flat, "♭"),
      (Natural, "")
    ).forEvery: (accidental, expected) =>
      TransformMusicalEvents.accidentalToString(accidental) shouldBe expected
end TransformMusicalEventsTest

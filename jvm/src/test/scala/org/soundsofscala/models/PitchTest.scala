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

package org.soundsofscala.models

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.soundsofscala.models.AtomicMusicalEvent.*
import org.soundsofscala.models.Pitch.*
import org.soundsofscala.syntax.all.*

class PitchTest extends AnyFunSuite with Matchers with TableDrivenPropertyChecks:

  test("Test Calculate Frequency"):

    Table(
      ("pitch", "frequency"),
      (Pitch.A, 440),
      (Pitch.B, 493.883),
      (Pitch.C, 261.626),
      (Pitch.D, 293.665),
      (Pitch.E, 329.628),
      (Pitch.F, 349.228),
      (Pitch.G, 391.995)
    ).forEvery((pitch, frequency) => pitch.calculateFrequency shouldBe frequency)

  test("Test Calculate Frequency with accidentals"):
    Table(
      ("note", "frequency"),
      (A4, 440),
      (A4.sharp, 466.1637615180899),
      (A4.flat, 415.3046975799451),
      (A8.sharp, 7458.620184289439),
      (B4, 493.883)).forEvery((note, frequency) => note.frequency shouldBe frequency)

  test("Frequency is correctly calculated from a Note with Octave"):
    Table(
      ("note", "frequency"),
      (A0, 27.5),
      (A1, 55),
      (A2, 110),
      (A3, 220),
      (A4, 440),
      (A5, 880),
      (A6, 1760),
      (A7, 3520),
      (A8, 7040),
      (B4, 493.883),
      (C4, 261.626),
      (D4, 293.665),
      (E4, 329.628),
      (F4, 349.228),
      (G4, 391.995),
      (C8, 4186.016),
      (D8, 4698.64),
      (E8, 5274.048)
    ).forEvery((note, frequency) => note.frequency shouldBe frequency)
end PitchTest

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

import cats.effect.IO
import org.soundsofscala.models.AtomicMusicalEvent.*
import org.soundsofscala.syntax.all.*
import weaver.SimpleIOSuite

object PitchTest extends SimpleIOSuite:

  test("calculate frequency for natural pitches"):
    val mappings = List(
      (Pitch.A, 440.0),
      (Pitch.B, 493.883),
      (Pitch.C, 261.626),
      (Pitch.D, 293.665),
      (Pitch.E, 329.628),
      (Pitch.F, 349.228),
      (Pitch.G, 391.995)
    )
    IO.pure(forEach(mappings): (pitch, frequency) =>
      expect(pitch.calculateFrequency == frequency))

  test("calculate frequency with accidentals"):
    val mappings = List(
      (A4, 440.0),
      (A4.sharp, 466.1637615180899),
      (A4.flat, 415.3046975799451),
      (A8.sharp, 7458.620184289439),
      (B4, 493.883)
    )
    IO.pure(forEach(mappings): (note, frequency) =>
      expect(note.frequency == frequency))

  test("frequency is correctly calculated from a Note with Octave"):
    val mappings = List(
      (A0, 27.5),
      (A1, 55.0),
      (A2, 110.0),
      (A3, 220.0),
      (A4, 440.0),
      (A5, 880.0),
      (A6, 1760.0),
      (A7, 3520.0),
      (A8, 7040.0),
      (B4, 493.883),
      (C4, 261.626),
      (D4, 293.665),
      (E4, 329.628),
      (F4, 349.228),
      (G4, 391.995),
      (C8, 4186.016),
      (D8, 4698.64),
      (E8, 5274.048)
    )
    IO.pure(forEach(mappings): (note, frequency) =>
      expect(note.frequency == frequency))

  test("octave doubling: each octave doubles frequency"):
    IO.pure(
      expect(A1.frequency == A0.frequency * 2) and
        expect(A2.frequency == A1.frequency * 2) and
        expect(A4.frequency == A3.frequency * 2))

  test("sharp raises frequency by one semitone"):
    val ratio = Math.pow(2, 1.0 / 12)
    IO.pure(expect(A4.sharp.frequency == A4.frequency * ratio))

  test("flat lowers frequency by one semitone"):
    val ratio = Math.pow(2, 1.0 / 12)
    IO.pure(expect(A4.flat.frequency == A4.frequency / ratio))
end PitchTest

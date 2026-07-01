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
import org.soundsofscala.models.Duration.*
import weaver.SimpleIOSuite

object DurationTest extends SimpleIOSuite:

  test("duration to seconds at 60 BPM"):
    val mappings = List(
      (Whole, 4.0),
      (Half, 2.0),
      (Quarter, 1.0),
      (Eighth, 0.5),
      (Sixteenth, 0.25),
      (ThirtySecond, 0.125),
      (SixtyFourth, 0.0625)
    )
    IO.pure(forEach(mappings): (duration, expected) =>
      expect(duration.toSeconds(Tempo(60)) == expected))

  test("duration to seconds at various tempos"):
    val mappings = List(
      (Whole, Tempo(112), 2.142857142857143),
      (Half, Tempo(117), 1.0256410256410255),
      (Quarter, Tempo(120), 0.5),
      (Eighth, Tempo(123), 0.24390243902439024),
      (Sixteenth, Tempo(126), 0.11904761904761904)
    )
    IO.pure(forEach(mappings): (duration, tempo, expected) =>
      expect(duration.toSeconds(tempo) == expected))

  test("duration to beats"):
    val mappings = List(
      (Whole, 4.0),
      (Half, 2.0),
      (Quarter, 1.0),
      (Eighth, 0.5),
      (Sixteenth, 0.25),
      (ThirtySecond, 0.125),
      (SixtyFourth, 0.0625)
    )
    IO.pure(forEach(mappings): (duration, expected) =>
      expect(duration.toBeats == expected))

  test("triplet durations are 2/3 of the next larger value"):
    IO.pure(
      expect(QuarterTriplet.toBeats == 2.0 / 3.0) and
        expect(EighthTriplet.toBeats == 1.0 / 3.0) and
        expect(SixteenthTriplet.toBeats == 0.5 / 3.0))

  test("dotted durations are 1.5x the base"):
    IO.pure(
      expect(WholeDotted.toBeats == 6.0) and
        expect(HalfDotted.toBeats == 3.0) and
        expect(QuarterDotted.toBeats == 1.5) and
        expect(EighthDotted.toBeats == 0.75) and
        expect(SixteenthDotted.toBeats == 0.375))

  test("duration halves with each step"):
    IO.pure(
      expect(Whole.toBeats == Half.toBeats * 2) and
        expect(Half.toBeats == Quarter.toBeats * 2) and
        expect(Quarter.toBeats == Eighth.toBeats * 2) and
        expect(Eighth.toBeats == Sixteenth.toBeats * 2) and
        expect(Sixteenth.toBeats == ThirtySecond.toBeats * 2) and
        expect(ThirtySecond.toBeats == SixtyFourth.toBeats * 2))

  test("doubling tempo halves duration in seconds"):
    IO.pure(
      expect(Quarter.toSeconds(Tempo(120)) == Quarter.toSeconds(Tempo(60)) / 2) and
        expect(Eighth.toSeconds(Tempo(120)) == Eighth.toSeconds(Tempo(60)) / 2))
end DurationTest

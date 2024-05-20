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
import org.soundsofscala.models.Duration.*

class DurationTest extends AnyFunSuite with Matchers with TableDrivenPropertyChecks:

  test("test duration to millisecond conversion") {
    val table = Table(
      ("note", "tempo", "expected"),
      (Whole, Tempo(60), 4),
      (Half, Tempo(60), 2),
      (Quarter, Tempo(60), 1),
      (Eighth, Tempo(60), .5),
      (Sixteenth, Tempo(60), .250),
      (ThirtySecond, Tempo(60), 0.125),
      (SixtyFourth, Tempo(60), 0.0625),
      (Whole, Tempo(112), 2.142857142857143),
      (Half, Tempo(117), 1.0256410256410255),
      (Quarter, Tempo(120), .5),
      (Eighth, Tempo(123), 0.24390243902439024),
      (Sixteenth, Tempo(126), 0.11904761904761904)
    )
    forAll(table)((note, tempo, seconds) => note.toSeconds(tempo) shouldBe seconds)

  }

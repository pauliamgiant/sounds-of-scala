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

enum Duration:
  case Whole,
    Half,
    Quarter,
    Eighth,
    Sixteenth,
    ThirtySecond,
    SixtyFourth,
    HalfTriplet,
    QuarterTriplet,
    EighthTriplet,
    SixteenthTriplet,
    ThirtySecondTriplet,
    WholeDotted,
    HalfDotted,
    QuarterDotted,
    EighthDotted,
    SixteenthDotted,
    ThirtySecondDotted

  /**
   * Essentiually divides a duration by three. Not availiable on triplets. Not available on all
   * durations.
   */
  def toTriplets: Duration = this match
    case Duration.Whole => Duration.HalfTriplet
    case Duration.Half => Duration.QuarterTriplet
    case Duration.Quarter => Duration.EighthTriplet
    case Duration.Eighth => Duration.SixteenthTriplet
    case Duration.Sixteenth => Duration.ThirtySecondTriplet
    case _ =>
      this

  private val oneThird: Double = 1d / 3d

  def toBeats: Double = this match
    case Duration.Whole => 4.0
    case Duration.Half => 2.0
    case Duration.Quarter => 1.0
    case Duration.Eighth => 0.5
    case Duration.Sixteenth => 0.25
    case Duration.ThirtySecond => 0.125
    case Duration.SixtyFourth => 0.0625
    // Triplets
    case Duration.HalfTriplet => 4.0 * oneThird
    case Duration.QuarterTriplet => 2.0 * oneThird
    case Duration.EighthTriplet => 1.0 * oneThird
    case Duration.SixteenthTriplet => 0.5 * oneThird
    case Duration.ThirtySecondTriplet => 0.25 * oneThird
    // Dotteds
    case Duration.WholeDotted => 4.0 * 1.5
    case Duration.HalfDotted => 2.0 * 1.5
    case Duration.QuarterDotted => 1.0 * 1.5
    case Duration.EighthDotted => 0.5 * 1.5
    case Duration.SixteenthDotted => 0.25 * 1.5
    case Duration.ThirtySecondDotted => 0.125 * 1.5

  def toSeconds(tempo: Tempo): Double =
    val secondsPerBeat = 60.0 / tempo.value
    this match
      case Duration.Whole => secondsPerBeat * 4
      case Duration.Half => secondsPerBeat * 2
      case Duration.Quarter => secondsPerBeat
      case Duration.Eighth => secondsPerBeat / 2
      case Duration.Sixteenth => secondsPerBeat / 4
      case Duration.ThirtySecond => secondsPerBeat / 8
      case Duration.SixtyFourth => secondsPerBeat / 16
      // Triplets
      case Duration.HalfTriplet => Duration.Whole.toSeconds(tempo) * oneThird
      case Duration.QuarterTriplet => Duration.Half.toSeconds(tempo) * oneThird
      case Duration.EighthTriplet => Duration.Quarter.toSeconds(tempo) * oneThird
      case Duration.SixteenthTriplet => Duration.Eighth.toSeconds(tempo) * oneThird
      case Duration.ThirtySecondTriplet => Duration.Sixteenth.toSeconds(tempo) * oneThird
      // Dotteds
      case Duration.WholeDotted => Duration.Whole.toSeconds(tempo) * 1.5
      case Duration.HalfDotted => Duration.Half.toSeconds(tempo) * 1.5
      case Duration.QuarterDotted => Duration.Quarter.toSeconds(tempo) * 1.5
      case Duration.EighthDotted => Duration.Eighth.toSeconds(tempo) * 1.5
      case Duration.SixteenthDotted => Duration.Sixteenth.toSeconds(tempo) * 1.5
      case Duration.ThirtySecondDotted => Duration.ThirtySecond.toSeconds(tempo) * 1.5
end Duration

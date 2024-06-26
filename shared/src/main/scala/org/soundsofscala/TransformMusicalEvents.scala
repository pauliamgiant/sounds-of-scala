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

import org.soundsofscala.models.Accidental.*
import org.soundsofscala.models.DrumVoice.*
import org.soundsofscala.models.Duration.*
import org.soundsofscala.models.*

object TransformMusicalEvents:
  def accidentalToString(accidental: Accidental): String = accidental match
    case Sharp => "#"
    case Flat => "♭"
    case Natural => ""

  def durationToString(duration: Duration, currentString: String): String =
    def formatSpaces(count: Int): String = " " * (count - currentString.length)
    duration match
      case Whole => currentString ++ formatSpaces(64 * 3)
      case Half => currentString ++ formatSpaces(32 * 3)
      case Quarter => currentString ++ formatSpaces(16 * 3)
      case Eighth => currentString ++ formatSpaces(8 * 3)
      case Sixteenth => currentString ++ formatSpaces(4 * 3)
      case ThirtySecond => currentString ++ formatSpaces(2 * 3)
      case SixtyFourth => currentString ++ formatSpaces(1 * 3)
      case HalfTriplet => currentString ++ formatSpaces(64)
      case QuarterTriplet => currentString ++ formatSpaces(32)
      case EighthTriplet => currentString ++ formatSpaces(16)
      case SixteenthTriplet => currentString ++ formatSpaces(8)
      case ThirtySecondTriplet => currentString ++ formatSpaces(4)
      case WholeDotted => currentString ++ formatSpaces((64 * 3 * 1.5).toInt)
      case HalfDotted => currentString ++ formatSpaces((32 * 3 * 1.5).toInt)
      case QuarterDotted => currentString ++ formatSpaces((16 * 3 * 1.5).toInt)
      case EighthDotted => currentString ++ formatSpaces((8 * 3 * 1.5).toInt)
      case SixteenthDotted => currentString ++ formatSpaces((4 * 3 * 1.5).toInt)
      case ThirtySecondDotted => currentString ++ formatSpaces((2 * 3 * 1.5).toInt)

  def drumVoiceToString(drum: DrumVoice): String = drum match
    case Kick => "Doob"
    case Snare => "Crack"
    case HiHatClosed => "Tsst"
    case HiHatOpen => "Tssssss"
    case Crash => "Pshhhh"
    case Ride => "Ting"
    case TomHigh => "Dim"
    case TomMid => "Dum"
    case FloorTom => "Duh"
    case Rimshot => "Tock"
    case Clap => "Clap"
    case Cowbell => "Ding"
    case Tambourine => "Chinka"
end TransformMusicalEvents

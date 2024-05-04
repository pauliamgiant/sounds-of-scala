package org.soundsofscala

import org.soundsofscala.models.*
import org.soundsofscala.models.Accidental.*
import org.soundsofscala.models.DrumVoice.*
import org.soundsofscala.models.Duration.*

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

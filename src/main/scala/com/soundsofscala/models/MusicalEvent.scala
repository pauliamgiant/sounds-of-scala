package com.soundsofscala.models

import com.soundsofscala.TransformMusicalEvents.*

import scala.annotation.targetName

enum MusicalEvent:
  // actions
  @targetName("combineMusicEvents")
  def +(other: MusicalEvent): MusicalEvent = this match
    case note: Note => Melody(note, other)
    case thisRest: Rest =>
      other match
        case thatRest: Rest => Melody(thisRest, thatRest)
        case _ => Melody(thisRest, other)
    case drum: DrumStroke => Melody(drum, other)
    case melody: Melody => Melody(melody, other)
    case harmony: Harmony => Melody(harmony, other)

  def printEvent(): String = this match
    case Note(pitch, accidental, duration, octave, velocity) =>
      val firstSection =
        s"$pitch${accidentalToString(accidental)}${octave.value}${velocity}_"
      durationToString(duration, firstSection)
    case Rest(duration) => durationToString(duration, "")
    case DrumStroke(drum, duration, velocity) =>
      val firstSection = s"${drumVoiceToString(drum)}${velocity}_"
      durationToString(duration, firstSection)
    case Melody(left, right) => left.printEvent() ++ right.printEvent()
    case Harmony(lower, upper) => lower.printEvent() ++ upper.printEvent()

  // description
  case Note(
      pitch: Pitch,
      accidental: Accidental,
      duration: Duration,
      octave: Octave,
      velocity: Velocity
  ) extends MusicalEvent
  case Rest(duration: Duration) extends MusicalEvent
  case DrumStroke(
      drum: DrumVoice,
      duration: Duration,
      velocity: Velocity
  ) extends MusicalEvent
  case Melody(left: MusicalEvent, right: MusicalEvent) extends MusicalEvent
  case Harmony(lower: MusicalEvent, upper: MusicalEvent) extends MusicalEvent

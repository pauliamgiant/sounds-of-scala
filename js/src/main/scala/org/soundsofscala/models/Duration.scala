package org.soundsofscala.models

enum Duration:
  case Whole, Half, Quarter, Eighth, Sixteenth, ThirtySecond, SixtyFourth

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

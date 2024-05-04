package org.soundsofscala.models

enum Duration:
  case Whole,
    HalfTriplet,
    Half,
    QuarterTriplet,
    Quarter,
    EighthTriplet,
    Eighth,
    SixteenthTriplet,
    Sixteenth,
    ThirtySecondTriplet,
    ThirtySecond,
    SixtyFourth

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

  def toSeconds(tempo: Tempo): Double =
    val secondsPerBeat = 60.0 / tempo.value
    this match
      case Duration.Whole => secondsPerBeat * 4
      case Duration.HalfTriplet => secondsPerBeat * 4 * (1d / 3)
      case Duration.Half => secondsPerBeat * 2
      case Duration.QuarterTriplet => secondsPerBeat * 2 * (1d / 3)
      case Duration.Quarter => secondsPerBeat
      case Duration.EighthTriplet => secondsPerBeat * (1d / 3)
      case Duration.Eighth => secondsPerBeat / 2
      case Duration.SixteenthTriplet => secondsPerBeat / 2 * (1d / 3)
      case Duration.Sixteenth => secondsPerBeat / 4
      case Duration.ThirtySecondTriplet => secondsPerBeat / 4 * (1d / 3)
      case Duration.ThirtySecond => secondsPerBeat / 8
      case Duration.SixtyFourth => secondsPerBeat / 16
end Duration

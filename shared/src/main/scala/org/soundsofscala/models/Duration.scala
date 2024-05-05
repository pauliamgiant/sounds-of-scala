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

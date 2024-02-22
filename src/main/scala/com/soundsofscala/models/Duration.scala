package com.soundsofscala.models

import scala.concurrent.duration.*
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

  private def timeConversion(noteTime: Double): FiniteDuration =
    BigDecimal(noteTime).setScale(0, BigDecimal.RoundingMode.HALF_UP).toInt.milliseconds

  def toTimeDuration(tempo: Tempo): FiniteDuration =

    val beatsPerMicroSecond = 60_000 / tempo.value

    this match
      case Duration.Whole =>
        timeConversion(beatsPerMicroSecond * 4)
      case Duration.Half =>
        timeConversion(beatsPerMicroSecond * 2)
      case Duration.Quarter =>
        timeConversion(beatsPerMicroSecond)
      case Duration.Eighth =>
        timeConversion(beatsPerMicroSecond / 2)
      case Duration.Sixteenth =>
        timeConversion(beatsPerMicroSecond / 4)
      case Duration.ThirtySecond =>
        timeConversion(beatsPerMicroSecond / 8)
      case Duration.SixtyFourth =>
        timeConversion(beatsPerMicroSecond / 16)

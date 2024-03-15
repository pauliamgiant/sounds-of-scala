package org.soundsofscala.models

enum Pitch(steps: Int):

  private val referenceFrequency: Double = 440.0 // A4 frequency in Hz

  // TODO: factor octave into calculation
  def calculateFrequency: Double =
    val result = referenceFrequency * Math.pow(2, this.steps / 12.0)
    BigDecimal(result).setScale(3, BigDecimal.RoundingMode.HALF_UP).toDouble

  case C extends Pitch(-9)
  case D extends Pitch(-7)
  case E extends Pitch(-5)
  case F extends Pitch(-4)
  case G extends Pitch(-2)
  case A extends Pitch(0)
  case B extends Pitch(2)

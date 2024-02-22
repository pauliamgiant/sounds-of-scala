package com.soundsofscala.models

enum Pitch(steps: Int):

  private val referenceFrequency: Double = 440.0 // A4 frequency in Hz
  // val ratio: Double = Math.pow(2, 1.0 / 12.0) // Ratio between semitones
  // def calculateFrequency(steps: Int) : Double = referenceFrequency * Math.pow(ratio, steps)

  def calculateFrequency: Double =
    val result = referenceFrequency * Math.pow(2, this.steps / 12.0)
    BigDecimal(result).setScale(3, BigDecimal.RoundingMode.HALF_UP).toDouble

  // A = 440 * 2 power of (0 / 12)
  // A = 440 * 2 power of 0
  // A = 440 * 1
  // A = 440

  // B = 440 * 2 power of (2 / 12)
  // B = 440 * 2 power of 0.16666666666666666
  // B = 440 * 1.122462048309373
  // B = 493.8833012561241

  // C = 440 * 2 power of (-9 / 12)
  // C = 440 * 2 power of -0.75
  // C = 440 * 0.5946035575013605
  // C = 261.6255653005986

  // for now we are ignoring octaves: C4-B4

  case C extends Pitch(-9)
  case D extends Pitch(-7)
  case E extends Pitch(-5)
  case F extends Pitch(-4)
  case G extends Pitch(-2)
  case A extends Pitch(0)
  case B extends Pitch(2)

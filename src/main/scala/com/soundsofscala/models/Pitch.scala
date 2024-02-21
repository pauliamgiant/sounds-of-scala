package com.soundsofscala.models

enum Pitch(steps: Int):

  val referenceFrequency: Double = 440.0 // A4 frequency in Hz
  // val ratio: Double = Math.pow(2, 1.0 / 12.0) // Ratio between semitones
  // def calculateFrequency(steps: Int) : Double = referenceFrequency * Math.pow(ratio, steps)

  def calculateFrequency: Double = 
    val result = referenceFrequency * Math.pow(2, steps / 12.0)
    BigDecimal(result).setScale(3, BigDecimal.RoundingMode.HALF_UP).toDouble

  // for now we are ignoring octaves: C4-B4

  case C extends Pitch(-9)
  case D extends Pitch(-7)
  case E extends Pitch(-5)
  case F extends Pitch(-4)
  case G extends Pitch(-2)
  case A extends Pitch(0)
  case B extends Pitch(2)

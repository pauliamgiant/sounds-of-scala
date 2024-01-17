package com.soundsofscala.models

import refined4s.Refined

type Octave = Octave.Type
object Octave extends Refined[Int] {
  override inline def invalidReason(a: Int): String =
    expectedMessage("Octave is an Int between -2 and 10")

  override inline def predicate(a: Int): Boolean =
    a >= -2 && a <= 10
}

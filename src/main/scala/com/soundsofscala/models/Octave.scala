package com.soundsofscala.models

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.*

final case class Octave(value: Int :| OctaveNumber)
type OctaveNumber = GreaterEqual[-2] & LessEqual[10]

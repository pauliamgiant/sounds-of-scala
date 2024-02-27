package com.soundsofscala.models

import com.soundsofscala.models.Octave.MidiVelocity

enum Velocity(val midiVelocity: MidiVelocity):
  def getNormalisedVelocity: Double =
    (1.0 / 127) * this.midiVelocity.value
  case TheSilentTreatment extends Velocity(MidiVelocity(0))
  case Softest extends Velocity(MidiVelocity(10))
  case Soft extends Velocity(MidiVelocity(50))
  case Medium extends Velocity(MidiVelocity(80))
  case Assertively extends Velocity(MidiVelocity(100))
  case Loud extends Velocity(MidiVelocity(115))
  case OnFull extends Velocity(MidiVelocity(127))

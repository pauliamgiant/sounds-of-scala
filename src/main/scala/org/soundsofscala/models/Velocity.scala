package org.soundsofscala.models

import Octave.MidiVelocity

enum Velocity(val midiVelocity: MidiVelocity):
  def getNormalisedVelocity: Double =
    (1.0 / 127) * this.midiVelocity.value
  case TheSilentTreatment extends Velocity(MidiVelocity(0))
  case Softest extends Velocity(MidiVelocity(16))
  case Soft extends Velocity(MidiVelocity(30))
  case Medium extends Velocity(MidiVelocity(75))
  case Assertively extends Velocity(MidiVelocity(90))
  case Loud extends Velocity(MidiVelocity(105))
  case Louder extends Velocity(MidiVelocity(118))
  case OnFull extends Velocity(MidiVelocity(127))
  case CiaoFinito extends Velocity(MidiVelocity(0))
  case Pianississimo extends Velocity(MidiVelocity(16))
  case Pianissimo extends Velocity(MidiVelocity(30))
  case Piano extends Velocity(MidiVelocity(45))
  case MezzoPiano extends Velocity(MidiVelocity(60))
  case MezzoForte extends Velocity(MidiVelocity(75))
  case Forte extends Velocity(MidiVelocity(90))
  case Fortissimo extends Velocity(MidiVelocity(105))
  case Fortississimo extends Velocity(MidiVelocity(127))

end Velocity

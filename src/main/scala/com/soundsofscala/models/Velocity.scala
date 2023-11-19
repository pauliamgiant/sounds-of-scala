package com.soundsofscala.models

enum Velocity(val midiVelocity: Int):
  case TheSilentTreatment extends Velocity(0)
  case Softest extends Velocity(10)
  case Soft extends Velocity(50)
  case Medium extends Velocity(80)
  case Assertively extends Velocity(100)
  case Loud extends Velocity(115)
  case OnFull extends Velocity(127)
  case OverDrive extends Velocity(500)
  case Napalm extends Velocity(13335)


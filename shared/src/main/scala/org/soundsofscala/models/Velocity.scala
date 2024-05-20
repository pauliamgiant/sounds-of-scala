/*
 * Copyright 2024 Sounds of Scala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.soundsofscala.models

import org.soundsofscala.models.Octave.MidiVelocity

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

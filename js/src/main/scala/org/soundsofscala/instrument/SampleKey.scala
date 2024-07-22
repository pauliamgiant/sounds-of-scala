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

package org.soundsofscala.instrument

import org.soundsofscala.models.*

final case class SampleKey(pitch: Pitch, accidental: Accidental, octave: Octave):
  override def toString: String =
    s"${pitch.toString}${accidental.toString}${octave.toString}"

  def frequency: Double =
    Freq.calculate(pitch, accidental, octave)

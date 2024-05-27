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

import cats.effect.IO
import org.scalajs.dom
import org.scalajs.dom.AudioContext
import org.soundsofscala
import org.soundsofscala.models
import org.soundsofscala.models.AtomicMusicalEvent.DrumStroke
import org.soundsofscala.models.DrumVoice.*
import org.soundsofscala.models.*
import org.soundsofscala.synthesis.DrumGeneration

final case class Simple80sDrumMachine() extends Instrument:
  def play(
      musicEvent: AtomicMusicalEvent,
      when: Double,
      attack: Attack,
      release: Release,
      tempo: Tempo)(using audioContext: dom.AudioContext): IO[Unit] =
    musicEvent match
      case drumStroke: AtomicMusicalEvent.DrumStroke =>
        drumStroke.drum match
          case Kick =>
            DrumGeneration.generateKick808(drumStroke, when)
          case Snare =>
            DrumGeneration.generateSnare808(drumStroke, when)
          case HiHatClosed =>
            DrumGeneration.generateHats808(drumStroke, when)
          case Clap =>
            DrumGeneration.generateClap808(drumStroke, when)
          case _ => DrumGeneration.generateKick808(drumStroke, when)
      case _ => IO.unit

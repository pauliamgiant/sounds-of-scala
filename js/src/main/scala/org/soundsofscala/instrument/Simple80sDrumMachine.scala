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
import org.soundsofscala
import org.soundsofscala.models
import org.soundsofscala.models.*
import org.soundsofscala.models.DrumVoice.*
import org.soundsofscala.synthesis.DrumGeneration
import cats.effect.Ref
import org.scalajs.dom.AudioNode

object Simple80sDrumMachine:
  def apply(): IO[Simple80sDrumMachine] =
    for
      activeNodesRef <- Ref.of[IO, Set[AudioNode]](Set.empty)
    yield new Simple80sDrumMachine(activeNodesRef)

final class Simple80sDrumMachine private (
    protected val activeNodesRef: Ref[IO, Set[AudioNode]]
) extends Instrument[Default.NoSettings]:

  final case class Settings(attack: Attack, release: Release)

  def playWithSettings(
      musicEvent: AtomicMusicalEvent,
      when: Double,
      tempo: Tempo,
      settings: Default.NoSettings)(using audioContext: dom.AudioContext): IO[Unit] =
    musicEvent match
      case drumStroke: AtomicMusicalEvent.DrumStroke =>
        drumStroke.drum match
          case Kick =>
            DrumGeneration.generateElectroKick(drumStroke, when)
          case Snare =>
            DrumGeneration.generateElectroSnare(drumStroke, when)
          case HiHatClosed =>
            DrumGeneration.generateElectroHiHat(drumStroke, when)
          case Clap =>
            DrumGeneration.generateElectroClap(drumStroke, when)
          case _ => DrumGeneration.generateElectroKick(drumStroke, when)
      case _ => IO.unit

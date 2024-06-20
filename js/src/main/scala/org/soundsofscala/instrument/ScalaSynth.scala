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
import org.soundsofscala.models.AtomicMusicalEvent.Note
import org.soundsofscala.models.DrumVoice.*
import org.soundsofscala.models.*
import org.soundsofscala.synthesis.Oscillator.*

final case class ScalaSynth()(using audioContext: AudioContext)
    extends Synth(using audioContext: AudioContext):
  override def attackRelease(when: Double, note: Note, tempo: Tempo, release: Release): IO[Unit] =
    IO:
      val keyNote = note.frequency
      val sineVelocity = note.velocity.getNormalisedVelocity
      val triangleVelocity = note.velocity.getNormalisedVelocity / 4
      val oscillators = Seq(
        SineOscillator(Frequency(keyNote), Volume(sineVelocity)),
        TriangleOscillator(Frequency(keyNote * 2), Volume(triangleVelocity))
      )
      oscillators.foreach: osc =>
        osc.play(when)
        osc.stop(when + note.durationToSeconds(tempo))

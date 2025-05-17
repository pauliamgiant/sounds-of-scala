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
import org.scalajs.dom.AudioContext
import org.soundsofscala
import org.soundsofscala.graph.AudioNode.*
import org.soundsofscala.graph.AudioParam
import org.soundsofscala.graph.AudioParam.AudioParamEvent.*
import org.soundsofscala.models
import org.soundsofscala.models.*
import org.soundsofscala.models.AtomicMusicalEvent.Note

final case class QuirkyFilterSynth()(using audioContext: AudioContext) extends Synth:
  override def attackRelease(
      when: Double,
      note: Note,
      tempo: Tempo,
      attack: Attack,
      release: Release,
      pan: Double): IO[Unit] =
    IO:
      val synthVelocity = note.velocity.getNormalisedVelocity
      println(s"Playing note ${note.frequency} at velocity $synthVelocity")

      val gainControl =
        Gain(
          List.empty,
          AudioParam(Vector(
            LinearRampToValueAtTime(synthVelocity, when),
            LinearRampToValueAtTime(0.0001, when + note.durationToSeconds(tempo)))))

      val delayFilter = bandPassFilter.withFrequency(AudioParam(Vector(
        ExponentialRampToValueAtTime(500, when),
        LinearRampToValueAtTime(6000, when + note.durationToSeconds(tempo))
      )))

      val osc1Saw =
        sawtoothOscillator(
          when,
          note.durationToSeconds(tempo)).withFrequency(
          AudioParam(Vector(SetValueAtTime(
            note.frequency,
            when))))

      val sixteenthPulse = note.durationToSeconds(tempo) / 4
      val panner = panControl.withPan(AudioParam(Vector(
        LinearRampToValueAtTime(-1, when + sixteenthPulse),
        LinearRampToValueAtTime(-0.5, when + sixteenthPulse * 2),
        LinearRampToValueAtTime(0.5, when + sixteenthPulse * 3),
        LinearRampToValueAtTime(1, when + sixteenthPulse * 4),
        LinearRampToValueAtTime(0, when + sixteenthPulse * 5),
        LinearRampToValueAtTime(-1, when + sixteenthPulse * 6),
        LinearRampToValueAtTime(-0.5, when + sixteenthPulse * 7),
        LinearRampToValueAtTime(0.5, when + sixteenthPulse * 8)
      )))

      val graph1 = osc1Saw --> delayFilter --> panner --> gainControl
      graph1.create
    .void
end QuirkyFilterSynth

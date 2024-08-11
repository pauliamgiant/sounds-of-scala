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
import org.soundsofscala.graph.AudioNode.*
import org.soundsofscala.graph.AudioParam
import org.soundsofscala.graph.AudioParam.AudioParamEvent.ExponentialRampToValueAtTime
import org.soundsofscala.graph.AudioParam.AudioParamEvent.LinearRampToValueAtTime
import org.soundsofscala.graph.AudioParam.AudioParamEvent.SetValueAtTime
import org.soundsofscala.models
import org.soundsofscala.models.AtomicMusicalEvent.Note
import org.soundsofscala.models.*

final case class ScalaSynth()(using audioContext: AudioContext)
    extends Synth(using audioContext: AudioContext):
  override def attackRelease(
      when: Double,
      note: Note,
      tempo: Tempo,
      attack: Attack,
      release: Release): IO[Unit] =
    IO:
      val synthVelocity = note.velocity.getNormalisedVelocity
      println(s"Playing note ${note.frequency} at velocity $synthVelocity")

      val filter = lowPassFilter.withFrequency(AudioParam(Vector(
        ExponentialRampToValueAtTime(1000, when),
        LinearRampToValueAtTime(10000, when + note.durationToSeconds(tempo)))))

      val squareGain =
        Gain(
          List.empty,
          AudioParam(Vector(
            ExponentialRampToValueAtTime(synthVelocity, when),
            LinearRampToValueAtTime(0.000001, when + note.durationToSeconds(tempo)))))

      val sawGain =
        Gain(
          List.empty,
          AudioParam(Vector(
            ExponentialRampToValueAtTime(synthVelocity / 4, when),
            LinearRampToValueAtTime(0.000001, when + note.durationToSeconds(tempo))))
        )

      val osc1Square =
        squareOscillator(
          when,
          note.durationToSeconds(tempo)).withFrequency(
          AudioParam(Vector(SetValueAtTime(
            note.frequency,
            when))))

      val osc2Saw =
        sawtoothOscillator(
          when,
          note.durationToSeconds(tempo)).withFrequency(
          AudioParam(Vector(SetValueAtTime(
            note.frequency,
            when))))

      val graph1 = osc1Square --> filter --> squareGain
      val graph2 = osc2Saw --> filter --> sawGain
      graph1.create
      graph2.create
    .void
end ScalaSynth

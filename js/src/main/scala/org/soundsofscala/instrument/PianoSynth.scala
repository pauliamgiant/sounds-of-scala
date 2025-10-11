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

import cats.effect.{IO, Ref}
import org.scalajs.dom.{AudioContext, AudioNode}
import org.soundsofscala.graph.AudioNode.Gain
import org.soundsofscala.graph.AudioNode.waveTableOscillator
import org.soundsofscala.graph.AudioParam
import org.soundsofscala.graph.AudioParam.AudioParamEvent.*
import org.soundsofscala.models
import org.soundsofscala.models.AtomicMusicalEvent.Note
import org.soundsofscala.models.*

import scala.scalajs.js

object PianoSynth:
  def apply()(using audioContext: AudioContext): IO[PianoSynth] =
    for
      activeNodesRef <- Ref.of[IO, Set[AudioNode]](Set.empty)
    yield new PianoSynth(activeNodesRef)

final class PianoSynth private (
    protected val activeNodesRef: Ref[IO, Set[AudioNode]]
)(using audioContext: AudioContext)
    extends Synth:

  override def attackRelease(
      when: Double,
      note: Note,
      tempo: Tempo,
      attack: Attack,
      release: Release,
      pan: Double,
      volume: Double): IO[Unit] =
    for
      createdNodes <- IO {
        /*
        are used to define the harmonic content of the waveform by specifying the coefficients
        for the real (cosine) and imaginary (sine) components of the Fourier series that represents the waveform.
         */
        // An array of cosine terms (traditionally the A terms).
        val realArray = new js.typedarray.Float32Array(js.Array(
          0, // DC offset
          0.6, // Fundamental frequency (C4)
          0.4, // 2nd harmonic (C5)
          0.2, // 3rd harmonic
          0.1, // 4th harmonic
          0.05, // 5th harmonic
          0.03 // 6th harmonic
        ).map(_.toFloat))
        // An array of sine terms (traditionally the B terms).
        val imaginaryArray = new js.typedarray.Float32Array(js.Array(
          0, // DC offset
          0.1, // Fundamental frequency (C4)
          0.05, // 2nd harmonic (C5)
          0.03, // 3rd harmonic
          0.02, // 4th harmonic
          0.01, // 5th harmonic
          0.005 // 6th harmonic
        ).map(_.toFloat))
        val velocity = note.velocity.getNormalisedVelocity
        val velocityModulatedVolume =
          volume * velocity * 0.5
        val keySoftenVelocity = velocityModulatedVolume / 2

        val wavetableOsc =
          waveTableOscillator(
            when = when,
            duration = note.durationToSeconds(tempo),
            realArray = realArray,
            imaginaryArray = imaginaryArray).withFrequency(
            AudioParam(Vector(SetValueAtTime(
              note.frequency,
              when))))
        val gainNode =
          Gain(
            List.empty,
            AudioParam(Vector(
              SetValueAtTime(
                0.0001,
                when
              ),
              ExponentialRampToValueAtTime(
                velocityModulatedVolume,
                when + 0.040
              ),
              LinearRampToValueAtTime(keySoftenVelocity, when + 0.080), // Gentle decay after 80ms
              ExponentialRampToValueAtTime(
                0.0001,
                when + (4 * note.durationToSeconds(tempo))
              )
            ))
          )
        val audioGraph = wavetableOsc --> gainNode
        val finalNode = audioGraph.create
        finalNode.connect(audioContext.destination)
        finalNode
      }
      nodes <- activeNodesRef.get
      _ <- activeNodesRef.set(nodes + createdNodes)
    yield ()
end PianoSynth

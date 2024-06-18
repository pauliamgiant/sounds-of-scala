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
import cats.implicits.*
import org.scalajs.dom
import org.scalajs.dom.AudioBuffer
import org.scalajs.dom.AudioContext
import org.soundsofscala.models.*
import scala.annotation.tailrec

final case class PianoSampler(samples: Map[SampleKey, AudioBuffer]) extends Sampler {

//order samples into a list of (frequency, SampleKey, AudioBuffer) tuples
  private val orderedSamples: Array[(Double, SampleKey, AudioBuffer)] =
    samples.map { case (key, buffer) => (key.frequency, key, buffer) }.toArray.sortBy {
      case (f, key, buffer) => f
    }

// Find the closest sample to the note we have been asked to play
  private def closestFrequency(
      sampleFreqs: Array[(Double, SampleKey, AudioBuffer)],
      target: Double): (Double, SampleKey, AudioBuffer) = {
    @tailrec
    def binarySearch(
        left: Int,
        right: Int,
        closest: (Double, SampleKey, AudioBuffer)): (Double, SampleKey, AudioBuffer) = {
      if left > right then closest
      else {
        val mid = left + (right - left) / 2
        val (f, key, buffer) = sampleFreqs(mid)
        val newClosest =
          if Math.abs(f - target) < Math.abs(closest._1 - target) then (f, key, buffer)
          else closest
        if f == target then (f, key, buffer)
        else if f < target then binarySearch(mid + 1, right, newClosest)
        else binarySearch(left, mid - 1, newClosest)
      }
    }
    binarySearch(0, sampleFreqs.length - 1, sampleFreqs(0))
  }

  def play(
      musicEvent: AtomicMusicalEvent,
      when: Double,
      attack: Attack,
      release: Release,
      tempo: Tempo)(using audioContext: dom.AudioContext): IO[Unit] = {
    musicEvent match {
      case note: AtomicMusicalEvent.Note =>
        val frequency = note.frequency
        val (closestF, closestKey, buffer) = closestFrequency(orderedSamples, frequency)
        val playbackRate = frequency / closestF
        Sampler.playSample(buffer, playbackRate, musicEvent, when)
      case _ => IO.println("This musical event is not a note.")
    }
  }
}

object PianoSampler {
  private val filePaths: List[(SampleKey, String)] =
    List(Pitch.C, Pitch.D, Pitch.E, Pitch.F, Pitch.G, Pitch.A, Pitch.B).flatMap { note =>
      List(
        SampleKey(
          note,
          Accidental.Natural,
          Octave(2))
          -> s"resources/audio/piano/${note}2.wav",
        SampleKey(
          note,
          Accidental.Natural,
          Octave(3)) -> s"resources/audio/piano/${note}3.wav"
      )
    }

  private def loadedSamplesMap(using AudioContext): IO[Map[SampleKey, AudioBuffer]] =
    filePaths.traverse {
      case (key, path) => SampleLoader.loadSample(path).map(buffer => key -> buffer)
    }.map(_.toMap)

  /**
   * Create a PianoSampler with the default sample set.
   */
  def default(using AudioContext): IO[PianoSampler] =
    loadedSamplesMap.map(samples => PianoSampler(samples))
}

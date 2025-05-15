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

final case class Sampler(samples: Map[SampleKey, AudioBuffer]) extends SamplePlayer:
  private val orderedSamples: Array[(Double, SampleKey, AudioBuffer)] =
    samples.map { case (key, buffer) => (key.frequency, key, buffer) }.toArray.sortBy {
      case (f, key, buffer) => f
    }

  private def closestFrequency(
      sampleFreqs: Array[(Double, SampleKey, AudioBuffer)],
      target: Double): (Double, SampleKey, AudioBuffer) =
    @tailrec
    def binarySearch(
        left: Int,
        right: Int,
        closest: (Double, SampleKey, AudioBuffer)): (Double, SampleKey, AudioBuffer) =
      if left > right then closest
      else
        val mid = left + (right - left) / 2
        val current = sampleFreqs(mid)
        val newClosest =
          if Math.abs(current._1 - target) < Math.abs(closest._1 - target) then current
          else closest
        if current._1 == target then current
        else if current._1 < target then binarySearch(mid + 1, right, newClosest)
        else binarySearch(left, mid - 1, newClosest)
    binarySearch(0, sampleFreqs.length - 1, sampleFreqs(0))

  protected def playWithSettings(
      musicEvent: AtomicMusicalEvent,
      when: Double,
      tempo: Tempo,
      settings: SamplePlayer.Settings)(using audioContext: dom.AudioContext): IO[Unit] =
    musicEvent match
      case note: AtomicMusicalEvent.Note =>
        val frequency = note.frequency
        val (closestF, closestKey, buffer) = closestFrequency(orderedSamples, frequency)
        val playbackRatePitchFix = frequency / closestF
        SamplePlayer.playSample(buffer, playbackRatePitchFix, musicEvent, when, settings, tempo)
      case harmony: AtomicMusicalEvent.Harmony =>
        harmony.notes.toList.traverse_ { harmonyTiming =>
          playWithSettings(
            harmonyTiming.note,
            when + harmonyTiming.timingOffset.value,
            tempo,
            settings)
        }

      case _ => IO.println("This musical event is not a note.")
end Sampler

object Sampler:

  def fromPaths(filePaths: List[(SampleKey, String)])(using AudioContext): IO[Sampler] =
    filePaths.traverse {
      case (key, path) => SampleLoader.loadSample(path).map(buffer => key -> buffer)
    }.map(samples => Sampler(samples.toMap))

  def piano(using AudioContext): IO[Sampler] =
    val filePaths: List[(SampleKey, String)] =
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
    fromPaths(filePaths)

  def guitar(using AudioContext): IO[Sampler] =
    val filePaths: List[(SampleKey, String)] =
      (List(Pitch.C, Pitch.D, Pitch.E, Pitch.F, Pitch.G).map { note =>
        SampleKey(note, Accidental.Natural, Octave(3)) -> s"resources/audio/guitar/${note}3.wav"
      }) ++ (List(Pitch.A, Pitch.B).map { note =>
        SampleKey(note, Accidental.Natural, Octave(2)) -> s"resources/audio/guitar/${note}2.wav"
      })
    fromPaths(filePaths)

  def bassGuitar(using AudioContext): IO[Sampler] =
    val filePaths: List[(SampleKey, String)] =
      List(Pitch.C, Pitch.D, Pitch.E, Pitch.F, Pitch.G, Pitch.A, Pitch.B).flatMap { note =>
        List(
          SampleKey(
            note,
            Accidental.Natural,
            Octave(0))
            -> s"resources/audio/bass-live/${note}0-Bass.wav",
          SampleKey(
            note,
            Accidental.Natural,
            Octave(1)) -> s"resources/audio/bass-live/${note}1-Bass.wav"
        )
      }
    fromPaths(filePaths)

  def rhubarb(using AudioContext): IO[Sampler] =
    val filePaths: List[(SampleKey, String)] = List(
      SampleKey(Pitch.C, Accidental.Natural, Octave(2)) -> "resources/audio/misc/rhubarbSample.wav"
    )
    fromPaths(filePaths)

  def vinyl(using AudioContext): IO[Sampler] =
    val filePaths: List[(SampleKey, String)] = List(
      SampleKey(Pitch.C, Accidental.Natural, Octave(3)) -> "resources/audio/misc/vinylNoise.wav"
    )
    fromPaths(filePaths)

  def sparkles(using AudioContext): IO[Sampler] =
    val filePaths: List[(SampleKey, String)] = List(
      SampleKey(Pitch.C, Accidental.Natural, Octave(3)) -> "resources/audio/misc/sparklesSample.wav"
    )
    fromPaths(filePaths)

  def kickDrum(using AudioContext): IO[Sampler] =
    val filePaths: List[(SampleKey, String)] = List(
      SampleKey(
        Pitch.C,
        Accidental.Natural,
        Octave(2)) -> "resources/audio/drums-electro/KickVintageElectro.wav"
    )
    fromPaths(filePaths)

  def snareDrum(using AudioContext): IO[Sampler] =
    val filePaths: List[(SampleKey, String)] = List(
      SampleKey(Pitch.C, Accidental.Natural, Octave(2)) -> "resources/audio/misc/snareShort.wav"
    )
    fromPaths(filePaths)

end Sampler

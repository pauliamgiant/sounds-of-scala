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

final case class Sampler(samples: Map[SampleKey, AudioBuffer]) extends SamplePlayer {

// Order samples into a list of (frequency, SampleKey, AudioBuffer) tuples
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
        val current = sampleFreqs(mid)
        val newClosest =
          if Math.abs(current._1 - target) < Math.abs(closest._1 - target) then current
          else closest
        if current._1 == target then current
        else if current._1 < target then binarySearch(mid + 1, right, newClosest)
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
        SamplePlayer.playSample(buffer, playbackRate, musicEvent, when)
      case _ => IO.println("This musical event is not a note.")
    }
  }
}

object Sampler {

  def fromPaths(filePaths: List[(SampleKey, String)])(using AudioContext): IO[Sampler] =
    filePaths.traverse {
      case (key, path) => SampleLoader.loadSample(path).map(buffer => key -> buffer)
    }.map(samples => Sampler(samples.toMap))

  def piano(using AudioContext): IO[Sampler] = {
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
  }

  def guitar(using AudioContext): IO[Sampler] = {
    val filePaths: List[(SampleKey, String)] = {
      (List(Pitch.C, Pitch.D, Pitch.E, Pitch.F, Pitch.G).map { note =>
        SampleKey(note, Accidental.Natural, Octave(3)) -> s"resources/audio/guitar/${note}3.wav"
      }) ++ (List(Pitch.A, Pitch.B).map { note =>
        SampleKey(note, Accidental.Natural, Octave(2)) -> s"resources/audio/guitar/${note}2.wav"
      })
    }
    fromPaths(filePaths)
  }

  def rhubarb(using AudioContext): IO[Sampler] = {
    val filePaths: List[(SampleKey, String)] = List(
      SampleKey(Pitch.C, Accidental.Natural, Octave(2)) -> s"resources/audio/misc/rhubarbSample.wav"
    )
    fromPaths(filePaths)
  }

  def vinyl(using AudioContext): IO[Sampler] = {
    val filePaths: List[(SampleKey, String)] = List(
      SampleKey(Pitch.C, Accidental.Natural, Octave(3)) -> "resources/audio/misc/vinylNoise.wav"
    )
    fromPaths(filePaths)
  }

  def sparkles(using AudioContext): IO[Sampler] = {
    val filePaths: List[(SampleKey, String)] = List(
      SampleKey(Pitch.C, Accidental.Natural, Octave(3)) -> "resources/audio/misc/sparklesSample.wav"
    )
    fromPaths(filePaths)
  }

  def kickDrum(using AudioContext): IO[Sampler] = {
    val filePaths: List[(SampleKey, String)] = List(
      SampleKey(Pitch.C, Accidental.Natural, Octave(2)) -> "resources/audio/misc/kickTL.wav"
    )
    fromPaths(filePaths)
  }

  def snareDrum(using AudioContext): IO[Sampler] = {
    val filePaths: List[(SampleKey, String)] = List(
      SampleKey(Pitch.C, Accidental.Natural, Octave(2)) -> "resources/audio/misc/snareShort.wav"
    )
    fromPaths(filePaths)
  }

  def rimShot(using AudioContext): IO[Sampler] = {
    val filePaths: List[(SampleKey, String)] = List(
      SampleKey(Pitch.C, Accidental.Natural, Octave(3)) -> "resources/audio/misc/rim.mp3"
    )
    fromPaths(filePaths)
  }
}

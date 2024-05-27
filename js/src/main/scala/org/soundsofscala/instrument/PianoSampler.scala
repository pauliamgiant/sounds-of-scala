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

final case class PianoSampler(samples: Map[SampleKey, AudioBuffer]) extends Sampler {
  def play(
      musicEvent: AtomicMusicalEvent,
      when: Double,
      attack: Attack,
      release: Release,
      tempo: Tempo)(using audioContext: dom.AudioContext): IO[Unit] = {
    musicEvent match {
      case note: AtomicMusicalEvent.Note =>
        // Find the closest sample to the note we have been asked to play
        // TODO: Use binary search
        // TODO: precalculate table of frequencies
        val frequency = note.frequency
        val frequencies =
          samples.keys.map(key => key.frequency -> key).toList.sortBy { case (f, key) => f }

        val (bestF, bestKey) =
          frequencies.foldLeft(frequencies.head) { (best, current) =>
            val (f, key) = best
            val (fCurrent, keyCurrent) = current
            val distFromBest = Math.abs(frequency - f)
            val distFromCurrent = Math.abs(frequency - fCurrent)

            if distFromCurrent < distFromBest then current else best
          }

        val playbackRate = bestF / frequency
        // TODO: Don't get the audio buffer; get it as we find the best sample to play back
        Sampler.playSample(samples(bestKey), playbackRate, musicEvent, when)
      case _ => IO.println("This musical event is not a note.")
    }
  }

  // def playSample(
  //     filePath: String,
  //     musicalEvent: AtomicMusicalEvent,
  //     when: Double): IO[Unit] =
  //   musicalEvent match {
  //     case note: AtomicMusicalEvent.Note =>
  //       val noteKey = SampleKey(note.pitch, note.accidental, note.octave)
  //     case _ => println("This musical event is not a note.")
  //   }

  //   def calculatePlaybackRate(musicalEvent: AtomicMusicalEvent): Double = {
  //     musicalEvent match {
  //       case note: AtomicMusicalEvent.Note =>
  //         val defaultFrequency = 261.626 // C4 (middle of piano) frequency in Hz
  //         // Frequency of the note
  //         val noteFrequency = note.pitch.calculateFrequency

  //         // Calculate the playback rate
  //         val playbackRate = noteFrequency / defaultFrequency

  //         playbackRate
  //       case _ => 1.0
  //     }
  //   }

  // println(s"Playing sample... $filePath")
  // println(s"musicalEvent: $musicalEvent")

  // loadedSamples.flatMap { samples =>
  //   samples.get(filePath) match
  //     case Some(buffer) =>
  //       for
  //         _ <- IO.println(s"PLAYING $filePath at $when")
  //         _ <- IO.println(s"Volume ${musicalEvent.normalizedVelocity}")
  //         gainNode <- IO(audioContext.createGain())
  //         sourceNode <- IO(audioContext.createBufferSource())
  //         _ <- IO(gainNode.gain.value = musicalEvent.normalizedVelocity / 2)
  //         _ <- IO(gainNode.connect(audioContext.destination))
  //         _ <- IO(sourceNode.connect(gainNode))
  //         _ <- IO {
  //           sourceNode.buffer = buffer
  //           sourceNode.start(when)
  //         }
  //       yield ()
  //     case None =>
  //       println(s"Sample not found... $filePath")
  //       println(s"Using default sample... $defaultSamplePath")
  //       loadedSamples.flatMap { samples =>
  //         samples.get(defaultSamplePath) match
  //           case Some(defaultBuffer) =>
  //             for
  //               _ <- IO.println(s"PLAYING pitched sample at $when")
  //               _ <- IO.println(s"Volume ${musicalEvent.normalizedVelocity}")
  //               gainNode <- IO(audioContext.createGain())
  //               sourceNode <- IO(audioContext.createBufferSource())
  //               _ <- IO(gainNode.gain.value = musicalEvent.normalizedVelocity / 2)
  //               _ <- IO(gainNode.connect(audioContext.destination))
  //               _ <- IO(sourceNode.connect(gainNode))
  //               _ <- IO {
  //                 sourceNode.buffer = defaultBuffer
  //                 sourceNode.playbackRate.value = calculatePlaybackRate(musicalEvent)
  //                 sourceNode.start(when)
  //               }
  //             yield ()
  //           case None =>
  //             IO.raiseError(FileLoadingError(s"Default sample $defaultSamplePath not loaded"))
  //       }
  // }
  // end playSample

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

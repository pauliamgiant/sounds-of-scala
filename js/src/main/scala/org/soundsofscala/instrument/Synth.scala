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
import cats.syntax.parallel.catsSyntaxParallelTraverse1
import org.scalajs.dom
import org.scalajs.dom.AudioContext
import org.soundsofscala
import org.soundsofscala.models
import org.soundsofscala.models.AtomicMusicalEvent.Harmony
import org.soundsofscala.models.AtomicMusicalEvent.Note
import org.soundsofscala.models.*

trait Synth(using AudioContext) extends Instrument[Synth.Settings]:

  def playWithSettings(
      musicEvent: AtomicMusicalEvent,
      when: Double,
      tempo: Tempo,
      settings: Synth.Settings)(using audioContext: dom.AudioContext): IO[Unit] =
    musicEvent match
      case note: AtomicMusicalEvent.Note =>
        this.attackRelease(when, note, tempo, settings.attack, settings.release, settings.pan)
      case chord: AtomicMusicalEvent.Harmony =>
        chord.notes.parTraverse(note =>
          playWithSettings(note.note, when, tempo, settings)).void
      case _ => IO.unit

  /**
   * Plays the note via an oscillator (or many)
   */
  def attackRelease(
      when: Double,
      note: Note,
      tempo: Tempo,
      attack: Attack,
      release: Release,
      pan: Double = 0.0): IO[Unit]
end Synth

object Synth:
  def apply()(using audioContext: AudioContext): Synth = ScalaSynth()
  def default()(using audioContext: AudioContext): Synth = Synth()

  final case class Settings(attack: Attack, release: Release, pan: Double)

  object Settings:
    given Default[Settings] with
      val default: Settings = Settings(Attack(0), Release(0.9), 0)
end Synth

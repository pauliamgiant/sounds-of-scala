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

package org.soundsofscala.models

import refined4s.Newtype
import refined4s.Refined

type Hertz = Hertz.Type

object Hertz extends Newtype[Double]

type Volume = Volume.Type

object Volume extends Newtype[Double]

type Bandwidth = Bandwidth.Type

object Bandwidth extends Newtype[Double]

type Title = Title.Type

object Title extends Newtype[String]

type FilePath = FilePath.Type

object FilePath extends Newtype[String]

type TimingOffset = TimingOffset.Type

object TimingOffset extends Newtype[Double]

type PauseOffset = PauseOffset.Type

object PauseOffset extends Newtype[Double]

type NextNoteTime = NextNoteTime.Type

object NextNoteTime extends Newtype[Double]

type StartTime = StartTime.Type

object StartTime extends Newtype[Double]

type Tempo = Tempo.Type
object Tempo extends Newtype[Double]

type Swing = Swing.Type

object Swing extends Refined[Int]:
  override inline def invalidReason(s: Int): String =
    expectedMessage("is an Int between 0 and 10. 0 is totally straight. 10 is very swung.")

  override inline def predicate(s: Int): Boolean =
    0 <= s && s <= 10

type Octave = Octave.Type

object Octave extends Refined[Int]:
  override inline def invalidReason(a: Int): String =
    expectedMessage("Octave is an Int between -2 and 10")

  override inline def predicate(a: Int): Boolean =
    a >= -2 && a <= 10

  type MidiVelocity = MidiVelocity.Type

  object MidiVelocity extends Refined[Int]:
    override inline def invalidReason(a: Int): String =
      expectedMessage(
        "Midi velocity is an Int between 0-127.. 7 bits of a byte.. Don't blame me.. this dates back to 1983.")

    override inline def predicate(a: Int): Boolean =
      a >= 0 && a <= 127

type Attack = Attack.Type
object Attack extends Refined[Double]:
  override inline def invalidReason(attack: Double): String =
    expectedMessage("Attack is a percentage between 0 and 1")

  override inline def predicate(attack: Double): Boolean =
    0.0 <= attack && attack <= 1

type Release = Release.Type

object Release extends Refined[Double]:
  override inline def invalidReason(release: Double): String =
    expectedMessage("Release is a percentage between 0.01 and 1")

  override inline def predicate(release: Double): Boolean =
    0.01 <= release && release <= 1

/**
 * Determines interval of how often to look ahead for the next note in milliseconds
 */
type LookAhead = LookAhead.Type

object LookAhead extends Refined[Double]:
  override inline def invalidReason(lookahead: Double): String =
    expectedMessage("lookahead must be between 0 and 1000 ms")

  override inline def predicate(lookahead: Double): Boolean =
    0 <= lookahead && lookahead <= 1000

/**
 * Determines size of the window in which to schedule notes in milliseconds
 */
type ScheduleWindow = ScheduleWindow.Type

object ScheduleWindow extends Refined[Double]:
  override inline def invalidReason(aheadWindow: Double): String =
    expectedMessage("schedule ahead time period must be between 0 and 10 seconds")

  override inline def predicate(aheadWindow: Double): Boolean =
    0 <= aheadWindow && aheadWindow <= 10

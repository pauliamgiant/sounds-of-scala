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

package org.soundsofscala.Instruments

import cats.effect.IO
import cats.syntax.all.*
import org.scalajs.dom
import org.scalajs.dom.AudioContext
import org.soundsofscala.models
import org.soundsofscala.models.*
import org.soundsofscala.transport.NoteScheduler

/**
 * Simplest possible drum machine that plays a sequence of drum events
 * @param tempo
 *   takes the speed of the music in beats per minute
 */
case class SimpleScala808DrumMachine(tempo: Tempo):
  def playGroove(
      drumMusicalEvents: MusicalEvent*
  )(using audioContext: AudioContext): IO[Unit] =
    drumMusicalEvents.parTraverse { drumMusicalEvent =>
      NoteScheduler(tempo, LookAhead(25), ScheduleWindow(0.1))
        .scheduleInstrument(drumMusicalEvent, SimpleDrums())
    }.void

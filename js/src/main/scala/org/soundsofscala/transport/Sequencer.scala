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

package org.soundsofscala.transport

import cats.effect.IO
import cats.syntax.all.*
import org.scalajs.dom.AudioContext
import org.soundsofscala.models.LookAhead
import org.soundsofscala.models.ScheduleWindow
import org.soundsofscala.models.Song

case class Sequencer():
  def playSong(song: Song)(using audioContext: AudioContext): IO[Unit] =
    val noteScheduler = NoteScheduler(song.tempo, LookAhead(25), ScheduleWindow(0.1))
    song
      .mixer
      .tracks
      .parTraverse: track =>
        noteScheduler.scheduleInstrument(track.musicalEvent, track.instrument, track.settings)
      .void

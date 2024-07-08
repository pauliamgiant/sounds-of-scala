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

import org.soundsofscala.instrument.Default
import org.soundsofscala.instrument.Instrument

case class Track[Settings](
    title: Title,
    musicalEvent: MusicalEvent,
    instrument: Instrument[Settings],
    customSettings: Option[Settings] = None,
    insertFX: List[FX] = List.empty,
    sendFX: List[FX] = List.empty)(using Default[Settings]) {
  val settings: Settings = customSettings.getOrElse(Default.default[Settings])
}

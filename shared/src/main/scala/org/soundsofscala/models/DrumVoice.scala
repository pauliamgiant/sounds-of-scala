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

enum DrumVoice:
  case Kick extends DrumVoice
  case Snare extends DrumVoice
  case HiHatClosed extends DrumVoice
  case HiHatOpen extends DrumVoice
  case Crash extends DrumVoice
  case Ride extends DrumVoice
  case FloorTom extends DrumVoice
  case TomMid extends DrumVoice
  case TomHigh extends DrumVoice
  case Rimshot extends DrumVoice
  case Clap extends DrumVoice
  case Cowbell extends DrumVoice
  case Tambourine extends DrumVoice

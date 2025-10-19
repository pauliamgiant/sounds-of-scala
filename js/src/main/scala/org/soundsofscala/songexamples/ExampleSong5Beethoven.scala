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

package org.soundsofscala.songexamples

import cats.effect.IO
import org.scalajs.dom.AudioContext
import org.soundsofscala.instrument
import org.soundsofscala.instrument.*
import org.soundsofscala.models.*
import org.soundsofscala.syntax.all.*

object ExampleSong5Beethoven:

  val customSettings: instrument.Synth.Settings =
    Synth.Settings(
      attack = Attack(0.1),
      release = Release(0.1),
      pan = 0,
      volume = 0.7
    )

  private val beginningChord = Chord(G3, D3, B2, G2, G1).half

  private val triplets =
    D3.eighthTriplet +
      G3.eighthTriplet +
      B3.eighthTriplet +
      D4.eighthTriplet +
      C4.eighthTriplet +
      A3.eighthTriplet

  private val bcTrillPart = B3.thirtySecond + C4.thirtySecond
  private val bcTrill = bcTrillPart + bcTrillPart
  private val trebClef1 =
    beginningChord + triplets + G3 + G3 + F3.sharp + G3 +
      A3.quarterDotted + A3.eighth + B3.quarterDotted + B3.eighth +
      D4 + C4.eighth + bcTrill + B3 + RestQuarter

  private val bassClef1 =
    RestWhole + RestQuarter + B2 + A2 + G2 +
      F2.sharp + D2 + G2 + F2 +
      E2 + F2.sharp +
      G2.eighth + G2.eighth + B2.eighth + D3.eighth

  private val cbTrillPart = C5.thirtySecond + B4.thirtySecond
  private val cbTrill = cbTrillPart + cbTrillPart
  private val trebClef2 =
    Chord(B3, D4, G4).half +
      D4.eighthTriplet +
      G4.eighthTriplet +
      B4.eighthTriplet +
      D5.eighthTriplet +
      C5.eighthTriplet +
      A4.eighthTriplet +
      G4 + G4 + F4.sharp + G4 + A4.quarterDotted + A4.eighth + B4.quarterDotted + B4.eighth + D5.quarter + C5.eighth + cbTrill +
      B4.eighth + C5.eighth +
      C5.sharp.eighth + D5.eighth +
      D5.sharp.eighth + E5.eighth +
      RestEighth + E5.eighth +
      D5.eighth + C5.eighth + B4.eighth + A4.eighth

  private val bassClef2 =
    Chord(G2, G3).half +
      RestHalf + RestQuarter +
      B3.eighth + D4.eighth +
      A3.eighth + D4.eighth +
      G3.eighth + D4.eighth +
      F3.sharp.eighth + D4.eighth +
      D3.eighth + D4.eighth +
      G3.eighth + D4.eighth +
      F3.eighth + D4.eighth +
      E3.eighth + G3.eighth +
      F3.sharp.eighth + A3.eighth +
      G3 + RestQuarter + RestQuarter +
      Chord(A3, E3, C3) +
      RestHalf

  private val trebleClef3 =
    C5.sharp.eighth + D5.eighth +
      RestEighth + D5.eighth +
      C5.eighth + B4.eighth +
      A4.eighth + G4.eighth +
      F4.sharp.eighth + E4.eighth +
      D4.eighth + C4.eighth +
      B3.eighth + A3.eighth +
      G3.eighth + F3.sharp.eighth +
      G3.quarterDotted + A3.eighth +
      B3.eighth + C4.eighth +
      C4.sharp.eighth + D4.eighth +
      D4.sharp.eighth + E4.eighth +
      RestEighth + E4.eighth +
      D4.eighth + C4.eighth +
      B3.eighth + A3.eighth +
      D4.sharp.eighth + E4.eighth +
      RestEighth + E4.eighth +
      E4.eighth + C4.sharp.eighth +
      A3.eighth + G3.eighth

  private val bassClef3 =
    RestQuarter + Chord(G3, D3, B2).quarter +
      RestHalf +
      C3 + RestQuarter + D3 + RestQuarter +
      G2 + RestQuarter + RestHalf +
      RestQuarter + Chord(C2, E2, A2).quarter +
      RestHalf +
      RestQuarter + Chord(C2.sharp, E2, A2).quarter +
      RestHalf

  private val tripletA =
    A4.eighthTriplet + F4.sharp.eighthTriplet + D4.eighthTriplet
  private val tripletB =
    A4.eighthTriplet + F4.sharp.eighthTriplet + C4.eighthTriplet
  private val tripletC = G4.eighthTriplet + D4.eighthTriplet + B3.eighthTriplet
  private val tripletD = D4.eighthTriplet + B3.eighthTriplet + G3.eighthTriplet

  private val trebleClef4 =
    F3.sharp + tripletA + C4 + tripletB +
      B3 + tripletC + G3 + tripletD +
      F3.sharp + tripletA + C4 + tripletB +
      B3 + tripletC + G3 + tripletD

  private val tripletEngineA =
    D2.eighthTriplet + F2.sharp.eighthTriplet + A2.eighthTriplet
  private val tripletEngineABar = tripletEngineA + tripletEngineA + tripletEngineA + tripletEngineA

  private val tripletEngineB =
    D2.eighthTriplet + G2.eighthTriplet + B2.eighthTriplet
  private val tripletEngineBBar = tripletEngineB + tripletEngineB + tripletEngineB + tripletEngineB

  private val bassClef4 =
    tripletEngineABar + tripletEngineBBar + tripletEngineABar + tripletEngineBBar

  private val upperVoice = trebClef1 + trebClef2 + trebleClef3 + trebleClef4
  private val lowerVoice = bassClef1 + bassClef2 + bassClef3 + bassClef4

  def song(): AudioContext ?=> IO[Song] =
    for
      sharedPiano <- PianoSynth()
    yield Song(
      title = Title("Something We All Know"),
      tempo = Tempo(110),
      swing = Swing(0),
      mixer = Mixer(
        Track(
          Title("Beethoven Upper Voice"),
          upperVoice,
          sharedPiano,
          customSettings = Some(customSettings)),
        Track(
          Title("Beethoven Lower Voice"),
          lowerVoice,
          sharedPiano,
          customSettings = Some(customSettings))
      )
    )
end ExampleSong5Beethoven

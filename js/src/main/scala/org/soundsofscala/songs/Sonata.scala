package org.soundsofscala.songs

import org.scalajs.dom.AudioContext
import org.soundsofscala
import org.soundsofscala.Instruments.*
import org.soundsofscala.models.*
import org.soundsofscala.syntax.all.*

// semibreve = 1
// minim = 1 / 2
// crotchet = 1 / 4
// quaver = 1 / 8
// semiquaver = 1 / 16
// demisemiquaver = 1 / 32
// hemidemisemiquaver = 1 / 64

object Sonata:
  private val beginningChord = Chord(G4, D4, B3, G3, G2).half

  private val triplets =
    D4.eighthTriplet +
      G4.eighthTriplet +
      B4.eighthTriplet +
      D5.eighthTriplet +
      C5.eighthTriplet +
      A4.eighthTriplet

  private val bcTrillPart = B4.thirtySecond + C5.thirtySecond
  private val bcTrill = bcTrillPart + bcTrillPart
  private val trebClef1 =
    beginningChord + triplets + G4 + G4 + F4.sharp + G4 +
      A4.quarterDotted + A4.eighth + B4.quarterDotted + B4.eighth +
      D5 + C5.eighth + bcTrill + B4 + RestQuarter

  private val bassClef1 =
    RestWhole + RestQuarter + B3 + A3 + G3 +
      F3.sharp + D3 + G3 + F3 +
      E3 + F3.sharp +
      G3.eighth + G3.eighth + B3.eighth + D4.eighth

  private val cbTrillPart = C6.thirtySecond + B5.thirtySecond
  private val cbTrill = cbTrillPart + cbTrillPart
  private val trebClef2 =
    Chord(B4, D5, G5).half +
      D5.eighthTriplet +
      G5.eighthTriplet +
      B5.eighthTriplet +
      D6.eighthTriplet +
      C6.eighthTriplet +
      A5.eighthTriplet +
      G5 + G5 + F5.sharp + G5 + A5.quarterDotted + A5.eighth + B5.quarterDotted + B5.eighth + D6.quarter + C6.eighth + cbTrill +
      B5.eighth + C6.eighth +
      C6.sharp.eighth + D6.eighth +
      D6.sharp.eighth + E6.eighth +
      RestEighth + E6.eighth +
      D6.eighth + C6.eighth + B5.eighth + A5.eighth

  private val bassClef2 =
    Chord(G3, G4).half +
      RestHalf + RestQuarter +
      B4.eighth + D5.eighth +
      A4.eighth + D5.eighth +
      G4.eighth + D5.eighth +
      F4.sharp.eighth + D5.eighth +
      D4.eighth + D5.eighth +
      G4.eighth + D5.eighth +
      F4.eighth + D5.eighth +
      E4.eighth + G4.eighth +
      F4.sharp.eighth + A4.eighth +
      G4 + RestQuarter + RestQuarter +
      Chord(A4, E4, C4) +
      RestHalf

  private val trebleClef3 =
    C6.sharp.eighth + D6.eighth +
      RestEighth + D6.eighth +
      C6.eighth + B5.eighth +
      A5.eighth + G5.eighth +
      F5.sharp.eighth + E5.eighth +
      D5.eighth + C5.eighth +
      B4.eighth + A4.eighth +
      G4.eighth + F4.sharp.eighth +
      G4.quarterDotted + A4.eighth +
      B4.eighth + C5.eighth +
      C5.sharp.eighth + D5.eighth +
      D5.sharp.eighth + E5.eighth +
      RestEighth + E5.eighth +
      D5.eighth + C5.eighth +
      B4.eighth + A4.eighth +
      D5.sharp.eighth + E5.eighth +
      RestEighth + E5.eighth +
      E5.eighth + C5.sharp.eighth +
      A4.eighth + G4.eighth

  private val bassClef3 =
    RestQuarter + Chord(G4, D4, B3).quarter +
      RestHalf +
      C4 + RestQuarter + D4 + RestQuarter +
      G3 + RestQuarter + RestHalf +
      RestQuarter + Chord(C3, E3, A3).quarter +
      RestHalf +
      RestQuarter + Chord(C3.sharp, E3, A3).quarter +
      RestHalf

  private val tripletA =
    A5.eighthTriplet + F5.sharp.eighthTriplet + D5.eighthTriplet
  private val tripletB =
    A5.eighthTriplet + F5.sharp.eighthTriplet + C5.eighthTriplet
  private val tripletC = G5.eighthTriplet + D5.eighthTriplet + B4.eighthTriplet
  private val tripletD = D5.eighthTriplet + B4.eighthTriplet + G4.eighthTriplet

  private val trebleClef4 =
    F4.sharp + tripletA + C5 + tripletB +
      B4 + tripletC + G4 + tripletD +
      F4.sharp + tripletA + C5 + tripletB +
      B4 + tripletC + G4 + tripletD

  private val tripletEngineA =
    D3.eighthTriplet + F3.sharp.eighthTriplet + A3.eighthTriplet
  private val tripletEngineABar = tripletEngineA + tripletEngineA + tripletEngineA + tripletEngineA

  private val tripletEngineB =
    D3.eighthTriplet + G3.eighthTriplet + B3.eighthTriplet
  private val tripletEngineBBar = tripletEngineB + tripletEngineB + tripletEngineB + tripletEngineB

  private val bassClef4 =
    tripletEngineABar + tripletEngineBBar + tripletEngineABar + tripletEngineBBar

  val upperVoice = trebClef1 + trebClef2 + trebleClef3 + trebleClef4
  val lowerVoice = bassClef1 + bassClef2 + bassClef3 + bassClef4

  def piece(): AudioContext ?=> Song =
    Song(
      title = Title("Beethoven Sonata No. 49 G Maj"),
      tempo = Tempo(140),
      swing = Swing(0),
      mixer = Mixer(
        Track(Title("TrebleClefPiano"), upperVoice, PianoSynth()),
        Track(Title("BassClefPiano"), lowerVoice, PianoSynth())
      )
    )
end Sonata

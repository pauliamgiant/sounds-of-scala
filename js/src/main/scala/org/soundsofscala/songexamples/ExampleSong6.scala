package org.soundsofscala.songexamples

import cats.effect.IO
import org.scalajs.dom.AudioContext
import org.soundsofscala.instrument.*
import org.soundsofscala.models.*
import org.soundsofscala.syntax.all.*

object ExampleSong6:

  private val triplet5DGATrebleClef = D5.sharp.eighthTriplet + G5.sharp.eighthTriplet + A5.sharp
  private val triplet5BAGTrebleClef = B5.sharp.eighthTriplet + A5.sharp.eighthTriplet + G5.sharp
  private val triplet655CAGTrebleClef = C6.sharp.eighthTriplet + A5.sharp.eighthTriplet + G5.sharp
  private val triplet655EABTrebleClef = E6.sharp.eighthTriplet + A5.sharp.eighthTriplet + B5.sharp

  private val measureOneTrebleClef =
    triplet5DGATrebleClef + triplet5BAGTrebleClef + triplet5DGATrebleClef
  private val measureTwoTrebleClef =
    triplet5BAGTrebleClef + triplet5DGATrebleClef + triplet5BAGTrebleClef
  private val measureThreeTrebleClef =
    triplet5DGATrebleClef + triplet655CAGTrebleClef + triplet655EABTrebleClef
  private val measureFourTrebleClef =
    triplet655CAGTrebleClef + triplet5DGATrebleClef + triplet5BAGTrebleClef

  def song(): AudioContext ?=> IO[Song] =
    for
      violinSynth <- ViolinSynth()
    yield Song(
      title = Title("Laideronnette, impératrice des pagodes"),
      tempo = Tempo(110),
      mixer = Mixer(
        Track(
          Title("Laideronnette, impératrice des pagodes"),
          measureOneTrebleClef + measureTwoTrebleClef + measureThreeTrebleClef + measureFourTrebleClef,
          violinSynth,
          Playback.OneShot
        )
      )
    )
end ExampleSong6

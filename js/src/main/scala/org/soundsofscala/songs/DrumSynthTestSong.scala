package org.soundsofscala.songs

import org.scalajs.dom.AudioContext
import org.soundsofscala
import org.soundsofscala.Instruments.SimpleDrumSynth
import org.soundsofscala.models.*
import org.soundsofscala.syntax.all.*

object DrumSynthTestSong:

  val kick: MusicalEvent = kk + r8 + r16 + kk.sixteenth + kk + r4
  val snare: MusicalEvent = (r4 + sn).repeat
  val hats: MusicalEvent =
    (hhc.eighth + hhc.sixteenth + hhc.sixteenth.softest).repeat(2)
      + hhc.eighth + hhc.eighthTriplet + hhc.sixteenthTriplet
      + hhc.eighthTriplet + hhc.eighthTriplet.p + hhc.eighthTriplet.mp

  def drumSynthSong(): AudioContext ?=> Song =
    Song(
      title = Title("Drum Synth Song"),
      tempo = Tempo(90),
      swing = Swing(0),
      mixer = Mixer(
        Track(Title("Kick"), kick.repeat(12), SimpleDrumSynth()),
        Track(Title("Snare"), snare.repeat(12), SimpleDrumSynth()),
        Track(Title("HiHats"), hats.repeat(12), SimpleDrumSynth())
      )
    )

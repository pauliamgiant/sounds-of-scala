package org.soundsofscala.songs

import org.scalajs.dom.AudioContext
import org.soundsofscala
import org.soundsofscala.Instruments.SimpleDrumSynth
import org.soundsofscala.models.*
import org.soundsofscala.syntax.all.*

object DrumSynthTestSong:

  val kick: MusicalEvent = kk + (r8triplet + r8triplet + kk.eighthTriplet) + kk + r4
  val snare: MusicalEvent = (r4 + sn).repeat
  val hats: MusicalEvent =
    (hhc.eighthTriplet + hhc.eighthTriplet.p + hhc.eighthTriplet.mp) * 4

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

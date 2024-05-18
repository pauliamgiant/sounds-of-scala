package org.soundsofscala.songexamples

import org.scalajs.dom.AudioContext
import org.soundsofscala
import org.soundsofscala.Instruments.SimpleDrums
import org.soundsofscala.models.*
import org.soundsofscala.syntax.all.*

object PolyRhythmicDrums extends SongExample:

  val simple: MusicalEvent = RestQuarter + SnareDrum + RestQuarter + SnareDrum

  val kick7Eight: MusicalEvent = kk + r8 + r16 + kk.sixteenth + kk + r8
  val snare: MusicalEvent = (r4 + sn).repeat
  val hats5Eight: MusicalEvent =
    (hhc.sixteenth + hhc.sixteenth.softest + hhc.eighth + hhc.eighth + hhc.sixteenth + hhc
      .sixteenth
      .softest + hhc.eighth).repeat(4)

  override def song(): AudioContext ?=> Song =
    Song(
      title = Title("PolyRhythmic Drums"),
      tempo = Tempo(112),
      swing = Swing(0),
      mixer = Mixer(
        Track(Title("Kick"), kick7Eight.repeat(12), SimpleDrums()),
        Track(Title("Snare"), snare.repeat(12), SimpleDrums()),
        Track(Title("HiHats"), hats5Eight.repeat(12), SimpleDrums())
      )
    )

package org.soundsofscala.songs

import org.scalajs.dom.AudioContext
import org.soundsofscala
import org.soundsofscala.Instruments.*
import org.soundsofscala.models.*
import org.soundsofscala.syntax.all.*

object ChromaticScalaSynthSong:

  val bassLine: MusicalEvent =
    A3.eighth + A3.sharp.eighth + B3.eighth + C4.eighth + C4.sharp.eighth + D4.eighth + D4
      .sharp
      .eighth + E4.eighth + F4.eighth + F4.sharp.eighth + G4.eighth + G4.sharp.eighth |
      A4.eighth + A4.sharp.eighth + B4.eighth + C5.eighth + C5.sharp.eighth + D5.eighth + D5
        .sharp
        .eighth + E5.eighth + F5.eighth + F5.sharp.eighth + G5.eighth + G5.sharp.eighth |
      A5.eighth + A5.sharp.eighth + B5.eighth + C6.eighth + C6.sharp.eighth + D6.eighth + D6
        .sharp
        .eighth + E6.eighth + F6.eighth + F6.sharp.eighth + G6.eighth + G6.sharp.eighth |
      A6.eighth + A6.sharp.eighth + B6.eighth + C7.eighth + C7.sharp.eighth + D7.eighth + D7
        .sharp
        .eighth + E7.eighth + F7.eighth + F7.sharp.eighth + G7.eighth + G7.sharp.eighth
  end bassLine

  val kickSequence: MusicalEvent =
    KickDrum + RestEighth + RestSixteenth + KickDrum.sixteenth + KickDrum + RestQuarter
  val snareSequence: MusicalEvent = (RestQuarter + SnareDrum).repeat
  val hatsSequence: MusicalEvent =
    (HatsClosed.eighth + HatsClosed.sixteenth + HatsClosed.sixteenth.softest).repeat(4)

  def chromaticScalaSynthSong(): AudioContext ?=> Song =
    Song(
      title = Title("Chromatic"),
      tempo = Tempo(92),
      swing = Swing(0),
      mixer = Mixer(
        Track(Title("Kick"), kickSequence.repeat(12), SimpleDrums()),
        Track(Title("Snare"), snareSequence.repeat(12), SimpleDrums()),
        Track(Title("HiHats"), hatsSequence.repeat(12), SimpleDrums()),
        Track(Title("Bass"), bassLine, ScalaSynth())
      )
    )

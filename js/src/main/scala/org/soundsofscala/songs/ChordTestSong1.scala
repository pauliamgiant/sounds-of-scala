package org.soundsofscala.songs

import org.scalajs.dom.AudioContext
import org.soundsofscala
import org.soundsofscala.Instruments.*
import org.soundsofscala.models.*
import org.soundsofscala.syntax.all.*

object ChordTestSong1 {

  val bassLine: MusicalEvent =
    val bar1 =
      G1.medium + RestEighth + RestSixteenth + G1.medium.sixteenth + G1.medium + RestQuarter
    val bar2 = bar1
    val bar3 =
      A2.medium + RestEighth + RestSixteenth + A2.medium.sixteenth + A(
        Octave(2)).medium + G1.medium
    val bar4 =
      F1.medium + RestEighth + RestSixteenth + F1.medium.sixteenth + F1.medium.half
    (bar1 + bar2 + bar3 + bar4).repeat(4)

  val piano: MusicalEvent =
    (Gmaj.whole.soft + Chord(G3.softest, B3.medium, E3.assertively).whole + Amin7
      .whole
      .soft + Fmaj7.whole.soft) repeat 4

  val kickSequence: MusicalEvent =
    KickDrum + RestEighth + RestSixteenth + KickDrum.sixteenth + KickDrum + RestQuarter
  val snareSequence: MusicalEvent = (RestQuarter + SnareDrum).repeat
  val hatsSequence: MusicalEvent =
    (HatsClosed.eighth + HatsClosed.sixteenth + HatsClosed.sixteenth.softest).repeat(4)
  val clapSequence: MusicalEvent = (RestQuarter + RestQuarter + SnareDrum).repeat

  def chordsSong(): AudioContext ?=> Song =
    Song(
      title = Title("Chords of Joy"),
      tempo = Tempo(92),
      swing = Swing(0),
      mixer = Mixer(
        Track(Title("Kick"), FourBarRest + kickSequence.repeat(12), SimpleDrums()),
        Track(Title("Snare"), FourBarRest + snareSequence.repeat(12), SimpleDrums()),
        Track(Title("HiHats"), hatsSequence.repeat(12), SimpleDrums()),
        Track(Title("Clap"), clapSequence.repeat(12), SimpleDrums()),
        Track(Title("Bass"), FourBarRest + bassLine, ScalaSynth()),
        Track(Title("Piano"), piano, SimplePiano())
      )
    )

}

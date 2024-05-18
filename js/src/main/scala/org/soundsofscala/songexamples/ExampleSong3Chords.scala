package org.soundsofscala.songexamples

import org.scalajs.dom.AudioContext
import org.soundsofscala
import org.soundsofscala.Instruments.*
import org.soundsofscala.models.*
import org.soundsofscala.syntax.all.*

object ExampleSong3Chords extends SongExample:

  val bassLine: MusicalEvent =
    val bar1 =
      G1.medium + RestEighth + RestSixteenth + G1.medium.sixteenth + G1.medium + RestQuarter
    val bar2 = bar1
    val bar3 =
      A1.medium + RestEighth + RestSixteenth + A1.medium.sixteenth + A(
        Octave(1)).medium + G1.medium
    val bar4 =
      F1.medium + RestEighth + RestSixteenth + F1.medium.sixteenth + F1.medium.half
    (bar1 + bar2 + bar3 + bar4).repeat(4)

  val piano: MusicalEvent =
    (Gmaj.whole.soft + Chord(G3.softest, B3.medium, E3.assertively).whole + Amin7
      .whole
      .soft + Fmaj7.whole.soft) repeat 4

  val kickSequence: MusicalEvent = kk + (r8triplet + r8triplet + kk.eighthTriplet) + kk + r4
  val snareSequence: MusicalEvent = (RestQuarter + SnareDrum).repeat
  val hatsSequence: MusicalEvent =
    (hhc.eighthTriplet.onFull + hhc.eighthTriplet.soft + hhc.eighthTriplet.soft) * 4

  val clapSequence: MusicalEvent = (RestQuarter + RestQuarter + RestQuarter + HandClap).repeat

  override def song(): AudioContext ?=> Song =
    Song(
      title = Title("Chords of Joy"),
      tempo = Tempo(92),
      swing = Swing(0),
      mixer = Mixer(
        Track(Title("Kick"), FourBarRest + kickSequence.repeat(12), SimpleDrumMachine()),
        Track(Title("Snare"), FourBarRest + snareSequence.repeat(12), SimpleDrumMachine()),
        Track(Title("HiHats"), hatsSequence.repeat(12), SimpleDrumMachine()),
        Track(Title("Clap"), clapSequence.repeat(12), SimpleDrumMachine()),
        Track(Title("Bass"), FourBarRest + bassLine, ScalaSynth()),
        Track(Title("Piano"), piano, SimplePiano())
      )
    )
end ExampleSong3Chords

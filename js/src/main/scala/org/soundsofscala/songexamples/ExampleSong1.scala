package org.soundsofscala.songexamples

import org.scalajs.dom.AudioContext
import org.soundsofscala
import org.soundsofscala.Instruments.*
import org.soundsofscala.models.*
import org.soundsofscala.models.AtomicMusicalEvent.{DrumStroke, Rest}
import org.soundsofscala.models.DrumVoice.*
import org.soundsofscala.models.Duration.*
import org.soundsofscala.models.Velocity.*
import org.soundsofscala.syntax.all.*

object ExampleSong1 extends SongExample {

  val bassLine: MusicalEvent =

    val bar1 =
      G(Octave(1)).medium + RestEighth + RestSixteenth + G(Octave(1)).soft.sixteenth + G(
        Octave(1)).medium + RestQuarter
    val bar2 = bar1
    val bar3 =
      A(Octave(1)).medium + RestEighth + RestSixteenth + A(Octave(1)).soft.sixteenth + A(
        Octave(1)).medium + G(Octave(1)).medium
    val bar4 =
      F(Octave(1)).medium + RestEighth + RestSixteenth + F(Octave(1)).soft.sixteenth + F(
        Octave(1)).medium.half
    (bar1 + bar2 + bar3 + bar4).repeat(4)

  val pianoTonicNotes: MusicalEvent =
    val bar1 = RestEighth + G3.softest + RestSixteenth + G3
      .softest
      .sixteenth + G3.softest + RestEighth + G3.softest.eighth
    val bar2 = RestEighth + B3.softest + RestSixteenth + B3
      .softest
      .sixteenth + B3.softest + RestEighth + B3.softest.eighth
    val bar3 =
      RestEighth + A3.softest + RestSixteenth + A3.softest.sixteenth + A3.softest + G3.softest
    val bar4 = F3.softest + RestEighth + RestSixteenth + F3.softest.sixteenth + F3.softest.half

    (bar1 + bar2 + bar3 + bar4).repeat(4)

  val piano = FourBarRest + (((D4.eighth.soft + B3.eighth.soft) * 4) |
    ((E4.eighth.soft + B3.eighth.soft) * 4) |
    ((E4.eighth.soft + C4.eighth.soft) * 4) |
    (G4.eighth.soft + C4.eighth.soft) * 2 + F4.eighth.soft + C4.eighth.soft + D4
      .eighth
      .soft + C4.eighth.soft) * 2

  val kick = Sequence(
    DrumStroke(Kick, Quarter, Medium),
    Sequence(
      Rest(Eighth),
      Sequence(
        Rest(Sixteenth),
        Sequence(
          DrumStroke(Kick, Sixteenth, Soft),
          Sequence(DrumStroke(Kick, Quarter, Medium), Rest(Quarter)))))
  ).loop

  val snare = Sequence(
    Rest(Quarter),
    Sequence(
      DrumStroke(Snare, Quarter, Medium),
      Sequence(Rest(Quarter), DrumStroke(Snare, Quarter, Loud)))).repeat(16)

  val clap =
    Sequence(Rest(Half), Sequence(Rest(Quarter), DrumStroke(Clap, Quarter, Assertively)))
      .repeat(16)

  val hats =
    Sequence(
      DrumStroke(HiHatClosed, Eighth, Loud),
      Sequence(
        DrumStroke(HiHatClosed, Sixteenth, Medium),
        DrumStroke(HiHatClosed, Sixteenth, Softest))).repeat(4).repeat(12)

  def song(): AudioContext ?=> Song =
    Song(
      title = Title("A Tale of Two Synths"),
      tempo = Tempo(92),
      swing = Swing(0),
      mixer = Mixer(
        Track(Title("Kick"), kick, SimpleDrumMachine()),
        Track(Title("Snare"), snare, SimpleDrumMachine()),
        Track(Title("HiHats"), hats, SimpleDrumMachine()),
        Track(Title("Bass"), bassLine, ScalaSynth()),
        Track(Title("PianoTonicNotes"), pianoTonicNotes, ScalaSynth()),
        Track(Title("Piano"), piano, ScalaSynth())
      )
    )
}

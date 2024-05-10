package org.soundsofscala.songs

import org.scalajs.dom.AudioContext
import org.soundsofscala
import org.soundsofscala.Instruments.*
import org.soundsofscala.models.*
import org.soundsofscala.models.AtomicMusicalEvent.{DrumStroke, Rest}
import org.soundsofscala.models.DrumVoice.*
import org.soundsofscala.models.Duration.*
import org.soundsofscala.models.Velocity.*
import org.soundsofscala.syntax.all.*

object TestSong1:

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
    val bar1 = RestEighth + G2.softest + RestSixteenth + G2
      .softest
      .sixteenth + G2.softest + RestEighth + G2.softest.eighth
    val bar2 = RestEighth + B2.softest + RestSixteenth + B2
      .softest
      .sixteenth + B2.softest + RestEighth + B2.softest.eighth
    val bar3 =
      RestEighth + A2.softest + RestSixteenth + A2.softest.sixteenth + A2.softest + G2.softest
    val bar4 = F2.softest + RestEighth + RestSixteenth + F2.softest.sixteenth + F2.softest.half

    (bar1 + bar2 + bar3 + bar4).repeat(4)

  val piano = FourBarRest + (((D3.eighth.soft + B2.eighth.soft) * 4) |
    ((E3.eighth.soft + B2.eighth.soft) * 4) |
    ((E3.eighth.soft + C3.eighth.soft) * 4) |
    (G3.eighth.soft + C3.eighth.soft) * 2 + F3.eighth.soft + C3.eighth.soft + D3
      .eighth
      .soft + C3.eighth.soft) * 2

  val kick = Sequence(
    DrumStroke(Kick, Quarter, Medium),
    Sequence(
      Rest(Eighth),
      Sequence(
        Rest(Sixteenth),
        Sequence(
          DrumStroke(Kick, Sixteenth, Soft),
          Sequence(DrumStroke(Kick, Quarter, Medium), Rest(Quarter)))))
  ).repeat(20)

  val snare = Sequence(
    Rest(Quarter),
    Sequence(
      DrumStroke(Snare, Quarter, Medium),
      Sequence(Rest(Quarter), DrumStroke(Snare, Quarter, Loud)))).repeat(20)

  val clap =
    Sequence(Rest(Half), Sequence(Rest(Quarter), DrumStroke(Clap, Quarter, Assertively)))
      .repeat(20)

  val hats =
    Sequence(
      DrumStroke(HiHatClosed, Eighth, Loud),
      Sequence(
        DrumStroke(HiHatClosed, Sixteenth, Medium),
        DrumStroke(HiHatClosed, Sixteenth, Softest))).repeat(4).repeat(20)

  def demoSong(): AudioContext ?=> Song =
    Song(
      title = Title("Test Tune 1"),
      tempo = Tempo(92),
      swing = Swing(0),
      mixer = Mixer(
        Track(Title("Kick"), kick, SimpleDrums()),
        Track(Title("Snare"), snare, SimpleDrums()),
        Track(Title("HiHats"), hats, SimpleDrums()),
        Track(Title("Clap"), clap, SimpleDrums()),
        Track(Title("Bass"), bassLine, ScalaSynth()),
        Track(Title("PianoTonicNotes"), pianoTonicNotes, SimplePiano()),
        Track(Title("Piano"), piano, SimplePiano())
      )
    )
end TestSong1

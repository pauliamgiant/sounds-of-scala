package com.soundsofscala.songs

import com.soundsofscala.Instruments.{ScalaSynth, SimpleDrums, SimplePiano}
import com.soundsofscala.models.*
import com.soundsofscala.models.Accidental.*
import com.soundsofscala.models.AtomicMusicalEvent.{DrumStroke, Note, Rest}
import com.soundsofscala.models.DrumVoice.*
import com.soundsofscala.models.Duration.*
import com.soundsofscala.models.MusicalEvent.FourBarRest
import com.soundsofscala.models.Velocity.*
import org.scalajs.dom.AudioContext

object TestSong1 {

  val bassLine: MusicalEvent =

    val A_16th = Note(Pitch.A, Natural, Sixteenth, Octave(3), Medium)
    val B_16th = Note(Pitch.B, Natural, Sixteenth, Octave(3), Medium)
    val C_16th = Note(Pitch.C, Natural, Sixteenth, Octave(2), Medium)
    val D_16th = Note(Pitch.D, Natural, Sixteenth, Octave(2), Medium)
    val E_16th = Note(Pitch.E, Natural, Sixteenth, Octave(2), Medium)
    val F_16th = Note(Pitch.F, Natural, Sixteenth, Octave(2), Medium)
    val G_16th = Note(Pitch.G, Natural, Sixteenth, Octave(2), Medium)

    val bar1 = (G_16th + G_16th + B_16th + D_16th) repeat 4
    val bar2 = bar1
    val bar3 = (A_16th + A_16th + B_16th + C_16th) repeat 4
    val bar4 = (F_16th + F_16th + A_16th + C_16th) repeat 4
    (bar1 + bar2 + bar3 + bar4).repeat(4)

  val piano =
    val C = Note(Pitch.C, Natural, Eighth, Octave(3), Soft)
    val D = Note(Pitch.D, Natural, Eighth, Octave(3), Soft)
    val B = Note(Pitch.B, Sharp, Eighth, Octave(2), Soft)
    val phrase1 = (Rest(Eighth) + C) repeat 4
    val phrase2 = (Rest(Eighth) + D) repeat 4
    val phrase3 = (Rest(Eighth) + B) repeat 4
    val section1 = (phrase1 repeat 3) + phrase2
    val section2 = (phrase3 repeat 2) + phrase1 + phrase2
    FourBarRest + section1 + section2

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
      title = Title("A test sequencer song"),
      tempo = Tempo(90),
      swing = Swing(0),
      mixer = Mixer(
        Track(Title("Kick"), kick, SimpleDrums()),
        Track(Title("Snare"), snare, SimpleDrums()),
        Track(Title("HiHats"), hats, SimpleDrums()),
        Track(Title("Clap"), clap, SimpleDrums()),
        Track(Title("Bass"), bassLine, ScalaSynth()),
        Track(Title("Piano"), piano, SimplePiano())
      )
    )

}

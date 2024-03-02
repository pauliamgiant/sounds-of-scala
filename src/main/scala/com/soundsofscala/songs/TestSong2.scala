package com.soundsofscala.songs

import com.soundsofscala.Instruments.*
import com.soundsofscala.models.*
import com.soundsofscala.models.Accidental.*
import com.soundsofscala.models.AtomicMusicalEvent.*
import com.soundsofscala.models.DrumVoice.*
import com.soundsofscala.models.Duration.*
import com.soundsofscala.models.Velocity.*
import org.scalajs.dom.AudioContext

object TestSong2 {

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

  def drumSynthSong(): AudioContext ?=> Song =
    Song(
      title = Title("A test sequencer song"),
      tempo = Tempo(90),
      swing = Swing(0),
      mixer = Mixer(
        Track(Title("Kick"), kick, SimpleDrumSynth()),
        Track(Title("Snare"), snare, SimpleDrumSynth()),
        Track(Title("HiHats"), hats, SimpleDrumSynth())
      )
    )

}

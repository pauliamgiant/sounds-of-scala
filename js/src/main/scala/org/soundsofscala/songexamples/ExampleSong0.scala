package org.soundsofscala.songexamples

import org.scalajs.dom.AudioContext
import org.soundsofscala.Instruments.ScalaSynth
import org.soundsofscala.models.*
import org.soundsofscala.syntax.all.*

object ExampleSong0 extends SongExample:

  val musicalEvent: MusicalEvent =
    C3 + C3 + G3 + G3 + A3 + A3 + G3.half |
      F3 + F3 + E3 + E3 + D3 + D3 + C3.half

  def song(): AudioContext ?=> Song =
    Song(
      title = Title("Something We All Know"),
      tempo = Tempo(110),
      swing = Swing(0),
      mixer = Mixer(
        Track(Title("Single Synth Voice"), musicalEvent, ScalaSynth())
      )
    )

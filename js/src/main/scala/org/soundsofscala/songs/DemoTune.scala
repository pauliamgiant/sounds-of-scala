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

object DemoTune:

  private val bassline: MusicalEvent =
    E1.medium * 4 + G1.medium * 4 + A1.medium * 4 + G1.medium * 48
  // harmonise piano in 3rds
  val piano: MusicalEvent = (G3.medium * 4 + B3.medium * 4 + C4.medium * 4 + B3.medium * 4) * 24

  val kick: MusicalEvent = (KickDrum.sixteenth + RestSixteenth).repeat(16)
  val snare: MusicalEvent = (RestQuarter + SnareDrum).repeat(24)
  val hats: MusicalEvent = HatsClosed.repeat(24)

  def demoTune(): AudioContext ?=> Song =
    Song(
      title = Title("Demo Tune"),
      tempo = Tempo(110),
      swing = Swing(0),
      mixer = Mixer(
        Track(Title("Kick"), kick, SimpleDrums()),
        Track(Title("Snare"), snare, SimpleDrums()),
        Track(Title("HiHats"), hats, SimpleDrums()),
        Track(Title("Bass"), bassline, ScalaSynth()),
        Track(Title("Piano"), bassline, SimplePiano())
      )
    )

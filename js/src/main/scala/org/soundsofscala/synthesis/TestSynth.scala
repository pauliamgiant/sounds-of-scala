package org.soundsofscala.synthesis

import org.scalajs.dom.AudioContext
import org.soundsofscala.synthesis.Oscillator.*
import org.soundsofscala.models.*

case class TestSynth()(using audioContext: AudioContext):
  val startingF = 440

  var oscillators: List[Oscillator] = List(
    SineOscillator(Frequency(440)),
    SawtoothOscillator(Frequency(startingF / 8)),
    SquareOscillator().frequency(Frequency(startingF / 4)),
    TriangleOscillator().frequency(Frequency(startingF - 3))
  )

  def play(time: Double): Unit =
    oscillators.foreach(_.start(time))

  def stop(): Unit =
    oscillators.foreach(_.stop(audioContext.currentTime))
    oscillators = List(
      SineOscillator(Frequency(440)),
      SawtoothOscillator(Frequency(startingF / 8)),
      SquareOscillator().frequency(Frequency(startingF / 4)),
      TriangleOscillator().frequency(Frequency(startingF - 3))
    )

  def updateFilterFrequency(f: Double): Unit =
    oscillators.head.updateFilterFrequency(f)
    oscillators(1).updateFilterFrequency(f)
    oscillators(2).updateFilterFrequency(f)
    oscillators(3).updateFilterFrequency(f)

  def updatePitchFrequency(f: Double): Unit =
    oscillators.head.updateFrequency(f)
    oscillators(1).updateFrequency(f / 4)
    oscillators(2).updateFrequency(f / 2)
    oscillators(3).updateFrequency(f - 3)

  def updateVolume(volume: Volume, waveType: WaveType): Unit =
    waveType match
      case WaveType.Sine =>
        oscillators.head.updateVolume(volume)
      case WaveType.Sawtooth =>
        oscillators(1).updateVolume(volume)
      case WaveType.Square =>
        oscillators(2).updateVolume(volume)
      case WaveType.Triangle =>
        oscillators(3).updateVolume(volume)

package com.soundsofscala.synthesis

import cats.effect.IO
import com.soundsofscala.models.{Note, Tempo}
import com.soundsofscala.synthesis.Oscillator.*
import org.scalajs.dom.AudioContext
import concurrent.duration.DurationInt

case class ScalaSynth()(using audioContext: AudioContext):

  def release(
      oscillators: Seq[Oscillator]
  ): Unit =
    println("Stopping ScalaSynth")
    oscillators.foreach(_.stop())

  private def majorThirdFrequency(f: Double): Double = {
    val semitoneRatio = math.pow(2, 1.0 / 12.0)
    val majorThirdRatio = math.pow(semitoneRatio, 4)
    f * majorThirdRatio
  }

  def attackRelease(note: Note, tempo: Tempo): IO[Unit] =
    val keyNote = note.pitch.calculateFrequency
    println(s"Playing ScalaSynth $keyNote at currentTime ${audioContext.currentTime}")

    val oscillators = Seq(
      SineOscillator(keyNote),
      SawtoothOscillator(keyNote / 4),
//      SawtoothOscillator(majorThirdFrequency(keyNote)),
      SquareOscillator(keyNote / 2),
      TriangleOscillator(keyNote - 3)
    )
    IO(oscillators.foreach(_.play())) >> IO.sleep(
      note.duration.toTimeDuration(tempo)
    ) >> IO(oscillators.foreach(_.stop()))

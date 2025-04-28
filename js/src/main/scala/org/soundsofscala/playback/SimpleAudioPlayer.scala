package org.soundsofscala.playback

import cats.effect.IO
import cats.syntax.all.*
import org.scalajs.dom
import org.scalajs.dom.{AudioBuffer, AudioBufferSourceNode, AudioContext, GainNode}
import org.soundsofscala.instrument.SampleLoader

case class SimpleAudioPlayer(
    path: String
)(using audioContext: AudioContext):

  private val audioBuffer: IO[AudioBuffer] = SampleLoader.loadSample(path)

  // scalafix:off DisableSyntax.var
  private var pauseOffset = 0.0
  private var sourceNode: Option[AudioBufferSourceNode] = none
  private var gainNode: Option[GainNode] = none
  private var startTime: Option[Double] = none
  // scalafix:on

  private val fadeTime = 0.02

  def play(): IO[Unit] =
    for
      _ <- stopPlayback()
      buffer <- audioBuffer
      source = audioContext.createBufferSource()
      gain = audioContext.createGain()
      _ = gain.gain.setValueAtTime(0.0, audioContext.currentTime)
      _ = gain.gain.linearRampToValueAtTime(1.0, audioContext.currentTime + fadeTime)
      _ = source.buffer = buffer
      _ = source.connect(gain)
      _ = gain.connect(audioContext.destination)
      _ = source.start(0, pauseOffset)
      _ = startTime = (audioContext.currentTime - pauseOffset).some
      _ = sourceNode = source.some
      _ = gainNode = gain.some
    yield ()

  def stop(): IO[Unit] =
    for
      _ <- stopPlayback()
      _ = pauseOffset = 0.0
      _ = startTime = none
    yield ()

  def pause(): IO[Unit] =
    for
      _ <- stopPlayback()
      _ = pauseOffset = audioContext.currentTime - startTime.getOrElse(0.0)
      _ <- IO.println(s"pauseOffset is: $pauseOffset")
    yield ()

  private def stopPlayback(): IO[Unit] =
    for
      _ <- IO.println(
        s"Calling stopPlayback. Source node is ${if sourceNode.isDefined then "" else "not"} defined.")
      _ = gainNode.map { gain =>
        val now = audioContext.currentTime
        gain.gain.setValueAtTime(gain.gain.value, now)
        gain.gain.linearRampToValueAtTime(0.0, now + fadeTime)
      }
      _ = sourceNode.map(_.stop(audioContext.currentTime + fadeTime))
      _ = sourceNode = none
      _ = gainNode = none
      _ = startTime = none
    yield ()

end SimpleAudioPlayer

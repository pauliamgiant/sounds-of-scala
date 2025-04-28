package org.soundsofscala.playback

import cats.effect.{IO, Ref}
import cats.syntax.all.*
import org.scalajs.dom
import org.scalajs.dom.{AudioBuffer, AudioBufferSourceNode, AudioContext, GainNode}
import org.soundsofscala.instrument.SampleLoader

case class FunctionalAudioPlayer(
    path: String,
    audioBuffer: AudioBuffer,
    pauseOffset: Ref[IO, Double],
    sourceNode: Ref[IO, Option[AudioBufferSourceNode]],
    gainNode: Ref[IO, Option[GainNode]],
    startTime: Ref[IO, Option[Double]]
)(using audioContext: AudioContext):

  private val fadeTime = 0.02

  def play(): IO[Unit] =
    for
      _ <- stopPlayback()
      source = audioContext.createBufferSource()
      gain = audioContext.createGain()
      offset <- pauseOffset.get
      _ <- IO {
        gain.gain.setValueAtTime(0.0, audioContext.currentTime)
        gain.gain.linearRampToValueAtTime(1.0, audioContext.currentTime + fadeTime)
        source.buffer = audioBuffer
        source.connect(gain)
        gain.connect(audioContext.destination)
        source.start(0, offset)
      }
      _ <- startTime.set((audioContext.currentTime - offset).some)
      _ <- sourceNode.set(source.some)
      _ <- gainNode.set(gain.some)
    yield ()

  def stop(): IO[Unit] =
    for
      _ <- stopPlayback()
      _ <- pauseOffset.set(0.0)
      _ <- startTime.set(None)
    yield ()

  def pause(): IO[Unit] =
    for
      _ <- stopPlayback()
      startOpt <- startTime.get
      offset = audioContext.currentTime - startOpt.getOrElse(0.0)
      _ <- pauseOffset.set(offset)
      _ <- IO.println(s"pauseOffset is: $offset")
    yield ()

  private def stopPlayback(): IO[Unit] =
    for
      sourceOpt <- sourceNode.get
      gainOpt <- gainNode.get
      _ <- IO.println(
        s"Calling stopPlayback. Source node is ${if sourceOpt.isDefined then "" else "not"} defined.")
      _ <- gainOpt.traverse { gain =>
        val now = audioContext.currentTime
        IO {
          gain.gain.setValueAtTime(gain.gain.value, now)
          gain.gain.linearRampToValueAtTime(0.0, now + fadeTime)
        }
      }
      _ <- sourceOpt.traverse(source => IO(source.stop(audioContext.currentTime + fadeTime)))
      _ <- sourceNode.set(None)
      _ <- gainNode.set(None)
      _ <- startTime.set(None)
    yield ()
end FunctionalAudioPlayer

object AudioPlayer:

  def apply(audioFilePath: String)(using audioContext: AudioContext): IO[FunctionalAudioPlayer] =
    for
      buffer <- SampleLoader.loadSample(audioFilePath)
      pauseOffset <- Ref.of[IO, Double](0.0)
      sourceNode <- Ref.of[IO, Option[AudioBufferSourceNode]](None)
      gainNode <- Ref.of[IO, Option[GainNode]](None)
      startTime <- Ref.of[IO, Option[Double]](None)
    yield FunctionalAudioPlayer(
      audioFilePath,
      buffer,
      pauseOffset,
      sourceNode,
      gainNode,
      startTime
    )

package org.soundsofscala.playback

import cats.effect.{IO, Ref}
import cats.syntax.all.*
import org.scalajs.dom
import org.scalajs.dom.{AudioBuffer, AudioBufferSourceNode, AudioContext}
import org.soundsofscala.instrument.SampleLoader

object SimpleAudioPlayer:
  def apply(path: String)(using audioContext: AudioContext): SimpleAudioPlayer =
    // In a Scala.js environment (browser, single-threaded)
    // Not doing concurrent, cross-thread mutation
    // No risk of preemptive effects or race conditions

    val pauseOffsetRef = Ref.unsafe[IO, Double](0.0)
    val sourceNodeRef = Ref.unsafe[IO, Option[AudioBufferSourceNode]](none)
    val startTimeRef = Ref.unsafe[IO, Option[Double]](none)

    new SimpleAudioPlayer(path, pauseOffsetRef, sourceNodeRef, startTimeRef)

case class SimpleAudioPlayer(
    path: String,
    pauseOffsetRef: Ref[IO, Double],
    sourceNodeRef: Ref[IO, Option[AudioBufferSourceNode]],
    startTimeRef: Ref[IO, Option[Double]]
)(using audioContext: AudioContext):

  private val audioBuffer: IO[AudioBuffer] = SampleLoader.loadSample(path)

  def play(): IO[Unit] =
    for
      _ <- stopPlayback()
      buffer <- audioBuffer
      source = audioContext.createBufferSource()
      pauseOffset <- pauseOffsetRef.get
      _ <- IO.println(s"pauseOffset at start of play method: $pauseOffset")
      _ <- IO {
        source.buffer = buffer
        source.connect(audioContext.destination)
        source.start(0, pauseOffset)

      }
      _ <- startTimeRef.set((audioContext.currentTime - pauseOffset).some)
      _ <- sourceNodeRef.set(source.some)
    yield ()

  def stop(): IO[Unit] =
    for
      _ <- stopPlayback()
      _ <- pauseOffsetRef.set(0.0)
      _ <- startTimeRef.set(none)
    yield ()

  def pause(): IO[Unit] =
    for
      startTime <- startTimeRef.get
      _ <- pauseOffsetRef.set(audioContext.currentTime - startTime.getOrElse(0.0))
      pauseOffset <- pauseOffsetRef.get
      _ = println(s"pauseOffset is: $pauseOffset")
      _ <- stopPlayback()
    yield ()

  private def stopPlayback(): IO[Unit] =
    for
      maybeNode <- sourceNodeRef.get
      _ <- IO.println(
        s"Calling stopPlayback. Source node is ${if maybeNode.isDefined then "" else "not"} defined: ")
      _ <- IO(maybeNode.foreach(_.stop()))
      _ <- sourceNodeRef.set(none)
      _ <- IO(startTimeRef.set(none))
    yield ()
end SimpleAudioPlayer

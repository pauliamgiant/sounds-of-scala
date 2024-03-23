package org.soundsofscala.transport

import cats.effect.IO
import org.scalajs.dom
import org.scalajs.dom.{AudioContext, XMLHttpRequest}
import org.soundsofscala.models.{AtomicMusicalEvent, FileLoadingError, MusicalEvent}

import scala.scalajs.js.typedarray.ArrayBuffer

case class SimpleSamplePlayer()(using audioContext: AudioContext):

  def playSample(filePath: String, musicEvent: AtomicMusicalEvent, when: Double): IO[Unit] =
    for
      _ <- IO.println(s"PLAYING $filePath at $when")
      request <- IO(dom.XMLHttpRequest())
      _ <- IO(request.open("GET", filePath, true))
      _ <- IO(request.responseType = "arraybuffer")
      _ <- loadAndPlaySample(request, musicEvent, when)
      _ <- IO.delay(request.send())
    yield IO.unit

  private def loadAndPlaySample(
      request: XMLHttpRequest,
      musicalEvent: AtomicMusicalEvent,
      when: Double): IO[Unit] =
    for
      gainNode <- IO(audioContext.createGain())
      sourceNode <- IO(audioContext.createBufferSource())
      _ <- IO(gainNode.gain.value = musicalEvent.normalizedVelocity / 2)
      _ <- IO(gainNode.connect(audioContext.destination))
      _ <- IO(sourceNode.connect(gainNode))
      _ <- IO(request.onload = (_: dom.Event) =>
        val arrayBuffer: ArrayBuffer = request.response match
          case ab: ArrayBuffer => ab
        audioContext.decodeAudioData(
          arrayBuffer,
          buffer =>
            sourceNode.buffer = buffer
            sourceNode.start(when)
          ,
          () => IO.raiseError(FileLoadingError("Error decoding audio data"))
        )
      )
    yield IO.unit

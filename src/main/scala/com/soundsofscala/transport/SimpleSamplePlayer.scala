package com.soundsofscala.transport

import cats.effect.IO
import com.soundsofscala.models.{AtomicMusicalEvent, MusicalEvent}
import org.scalajs.dom
import org.scalajs.dom.AudioContext

import scala.scalajs.js.typedarray.ArrayBuffer

case class SimpleSamplePlayer()(using audioContext: AudioContext) {
  def playSample(filePath: String, musicEvent: AtomicMusicalEvent, when: Double): IO[Unit] =
    println(s"PLAYING $filePath at $when")
    val request = new dom.XMLHttpRequest()
    request.open("GET", filePath, true)
    request.responseType = "arraybuffer"

    request.onload = (_: dom.Event) =>
      val data = request.response.asInstanceOf[ArrayBuffer]

      audioContext.decodeAudioData(
        data,
        buffer => {
          val gainNode = audioContext.createGain()
          gainNode.gain.value = musicEvent.normalizedVelocity
          val sourceNode = audioContext.createBufferSource()
          sourceNode.buffer = buffer
          sourceNode.connect(gainNode)
          gainNode.connect(audioContext.destination)
          sourceNode.start(when)
        },
        () => println(s"Things have gone sideways for now")
      )
    IO.delay(request.send())
}

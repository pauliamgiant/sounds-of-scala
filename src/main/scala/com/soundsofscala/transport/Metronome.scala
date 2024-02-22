package com.soundsofscala.transport

import cats.effect.IO
import com.soundsofscala.models.*
import com.soundsofscala.types.{LookAhead, NextNoteTime, ScheduleWindow, Tempo}
import org.scalajs.dom
import org.scalajs.dom.AudioContext

import scala.concurrent.duration.DurationDouble
import scala.scalajs.js.typedarray.ArrayBuffer

case class Metronome(
    tempo: Tempo,
    lookAheadMs: LookAhead,
    scheduleAheadTimeSeconds: ScheduleWindow) {

  def playClick(duration: Duration)(using audioContext: AudioContext): IO[Unit] = {
    val initialNextNoteValue = NextNoteTime(audioContext.currentTime)
    audioContext.resume()
    scheduler("resources/audio/drums/NeonSnare.wav", duration, initialNextNoteValue)

  }

  def scheduler(path: String, noteLength: Duration, nextNoteTime: NextNoteTime)(
      using audioContext: AudioContext): IO[Unit] = {

    def loop(nextNoteTime: NextNoteTime): IO[Unit] =
      if (nextNoteTime.value < audioContext.currentTime + scheduleAheadTimeSeconds.value) {
        for {
          _ <- scheduleNote(nextNoteTime.value, path)
          _ <- loop(NextNoteTime(nextNoteTime.value + noteLength.toSeconds(tempo)))
        } yield IO.unit
      } else {
        IO.sleep(lookAheadMs.value.millis) >> scheduler(path, noteLength, nextNoteTime)
      }
    loop(nextNoteTime)
  }

  def scheduleNote(time: Double, path: String)(using audioContext: AudioContext): IO[Unit] =
    println(s"${audioContext.currentTime}: Scheduling note to play at $time with path $path")
    playDrum(time, path)

  def playDrum(time: Double, filePath: String)(using audioContext: AudioContext): IO[Unit] = {
    println(s"Playing note at $time")

    val request = new dom.XMLHttpRequest()
    request.open("GET", filePath, true)
    request.responseType = "arraybuffer"

    request.onload = (_: dom.Event) =>
      val data = request.response.asInstanceOf[ArrayBuffer]

      audioContext.decodeAudioData(
        data,
        buffer => {
          val gainNode = audioContext.createGain()
          gainNode.gain.value = 0.5
          val sourceNode = audioContext.createBufferSource()
          sourceNode.buffer = buffer
          sourceNode.connect(gainNode)
          gainNode.connect(audioContext.destination)
          sourceNode.start(time)
        },
        () => println(s"Things have gone sideways for now")
      )
    IO.delay(request.send())
  }
}

package com.soundsofscala.transport

import cats.effect.IO
import com.soundsofscala.models.*
import com.soundsofscala.models.AtomicMusicalEvent.*
import com.soundsofscala.types.{LookAhead, NextNoteTime, ScheduleWindow, Tempo}
import org.scalajs.dom
import org.scalajs.dom.AudioContext

import scala.concurrent.duration.DurationDouble
import scala.scalajs.js.typedarray.ArrayBuffer

case class NoteScheduler(
    tempo: Tempo,
    lookAheadMs: LookAhead,
    scheduleAheadTimeSeconds: ScheduleWindow) {

  enum ScheduleState {
    case Ready extends ScheduleState
    case Waiting extends ScheduleState
  }

  object ScheduleState {
    def apply(nextNoteTime: NextNoteTime)(using audioContext: AudioContext): ScheduleState =
      if nextNoteTime.value < audioContext.currentTime + scheduleAheadTimeSeconds.value then
        Ready
      else Waiting

  }

  def playVoice(musicalEvent: MusicalEvent)(using audioContext: AudioContext): IO[Unit] = {
    val initialNextNoteValue = NextNoteTime(audioContext.currentTime)
    audioContext.resume()
    scheduler(musicalEvent, initialNextNoteValue) >> IO.println("And we're done!")

  }

  def scheduler(musicalEvent: MusicalEvent, nextNoteTime: NextNoteTime)(
      using audioContext: AudioContext): IO[Unit] = {

    def loop(musicalEvent: MusicalEvent, nextNoteTime: NextNoteTime): IO[Unit] =
      musicalEvent match
        case Sequence(head, tail) =>
          ScheduleState(nextNoteTime) match
            case ScheduleState.Ready =>
              for {
                _ <- scheduler(head, nextNoteTime)
                _ <- loop(
                  tail,
                  NextNoteTime(nextNoteTime.value + head.durationToSeconds(tempo)))
              } yield IO.unit
            case ScheduleState.Waiting =>
              IO.sleep(lookAheadMs.value.millis) >> loop(musicalEvent, nextNoteTime)

        case _: AtomicMusicalEvent =>
          ScheduleState(nextNoteTime) match
            case ScheduleState.Ready =>
              for {
                _ <- scheduleNote(nextNoteTime.value)
              } yield IO.unit
            case ScheduleState.Waiting =>
              IO.sleep(lookAheadMs.value.millis) >> loop(musicalEvent, nextNoteTime)

    loop(musicalEvent, nextNoteTime)

  }

  def scheduleNote(time: Double)(using audioContext: AudioContext): IO[Unit] =
    println(s"${audioContext.currentTime}: Scheduling note to play at $time")
    playDrum(time, "resources/audio/drums/NeonKick.wav")

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

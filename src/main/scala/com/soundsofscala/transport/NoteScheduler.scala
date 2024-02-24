package com.soundsofscala.transport

import cats.effect.IO
import com.soundsofscala.models
import com.soundsofscala.models.*
import com.soundsofscala.models.AtomicMusicalEvent.*
import org.scalajs.dom
import org.scalajs.dom.AudioContext

import scala.concurrent.duration.DurationDouble
import scala.scalajs.js.typedarray.ArrayBuffer

case class NoteScheduler(
    tempo: Tempo,
    lookAheadMs: LookAhead,
    scheduleAheadTimeSeconds: ScheduleWindow):

  enum ScheduleState:
    case Ready extends ScheduleState
    case Waiting extends ScheduleState

  object ScheduleState:
    def apply(nextNoteTime: NextNoteTime)(using audioContext: AudioContext): ScheduleState =
      if nextNoteTime.value < audioContext.currentTime + scheduleAheadTimeSeconds.value then
        Ready
      else Waiting

  def playVoice(musicalEvent: MusicalEvent)(using audioContext: AudioContext): IO[Unit] =
    val initialNextNoteValue = NextNoteTime(audioContext.currentTime)
    scheduler(musicalEvent, initialNextNoteValue) >> IO.println("Sequence finished")

  def scheduler(musicalEvent: MusicalEvent, nextNoteTime: NextNoteTime)(
      using audioContext: AudioContext): IO[Unit] =
    musicalEvent match
      case Sequence(head, tail) =>
        ScheduleState(nextNoteTime) match
          case ScheduleState.Ready =>
            for
              _ <- scheduler(head, nextNoteTime)
              _ <- scheduler(
                tail,
                NextNoteTime(nextNoteTime.value + head.durationToSeconds(tempo)))
            yield IO.unit
          case ScheduleState.Waiting =>
            IO.sleep(lookAheadMs.value.millis) >> scheduler(musicalEvent, nextNoteTime)

      case event: AtomicMusicalEvent =>
        ScheduleState(nextNoteTime) match
          case ScheduleState.Ready =>
            event match
              case note: AtomicMusicalEvent.Note =>
                for {
                  _ <- scheduleNote(nextNoteTime.value, note)
                } yield IO.unit
              case drumStroke: AtomicMusicalEvent.DrumStroke =>
                for {
                  _ <- scheduleDrum(nextNoteTime.value, drumStroke)
                } yield IO.unit
              case AtomicMusicalEvent.Rest(_) =>
                IO.unit
              // TODO implement Chords
              case AtomicMusicalEvent.Harmony(_, _) =>
                IO.unit
          case ScheduleState.Waiting =>
            IO.sleep(lookAheadMs.value.millis) >> scheduler(musicalEvent, nextNoteTime)

  def scheduleNote(time: Double, musicEvent: Note)(using audioContext: AudioContext): IO[Unit] =
    println(s"${audioContext.currentTime}: Scheduling note to play at $time")
    playNote(time, musicEvent)

  def scheduleDrum(time: Double, musicEvent: DrumStroke)(
      using audioContext: AudioContext): IO[Unit] =
    println(s"${audioContext.currentTime}: Scheduling drum to play at $time")
    NoteScheduler.playDrum(time, musicEvent)

  def playNote(time: Double, note: Note)(using audioContext: AudioContext): IO[Unit] =
    println(s"Playing note at $time")
    val octave = note.octave.value
    val pitch: String = note.pitch.toString
    val filePath = s"resources/audio/piano/$pitch$octave.wav"

    val request = new dom.XMLHttpRequest()
    request.open("GET", filePath, true)
    request.responseType = "arraybuffer"

    request.onload = (_: dom.Event) =>
      val data = request.response.asInstanceOf[ArrayBuffer]

      audioContext.decodeAudioData(
        data,
        buffer =>
          val gainNode = audioContext.createGain()
          gainNode.gain.value = 0.5
          val sourceNode = audioContext.createBufferSource()
          sourceNode.buffer = buffer
          sourceNode.connect(gainNode)
          gainNode.connect(audioContext.destination)
          sourceNode.start(time)
        ,
        () => println(s"Things have gone sideways for now")
      )
    IO.delay(request.send())

object NoteScheduler:
  def playDrum(time: Double, drumStroke: DrumStroke)(
      using audioContext: AudioContext): IO[Unit] = {
    println(s"Playing drum at $time")
    val filePath = drumStroke.drum match
      case models.DrumVoice.Kick => "resources/audio/drums-808/Kick808.wav"
      case models.DrumVoice.Snare => "resources/audio/drums-808/Snare808.wav"
      case models.DrumVoice.HiHatClosed => "resources/audio/drums-808/Hats808.wav"
      case models.DrumVoice.Clap => "resources/audio/drums-808/Clap808.wav"
      case _ => "resources/audio/drums-808/G.wav"

    val request = new dom.XMLHttpRequest()
    request.open("GET", filePath, true)
    request.responseType = "arraybuffer"

    request.onload = (_: dom.Event) =>
      val data = request.response.asInstanceOf[ArrayBuffer]

      audioContext.decodeAudioData(
        data,
        buffer => {
          val gainNode = audioContext.createGain()
          gainNode.gain.value = drumStroke.velocity.getNormalisedVelocity
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

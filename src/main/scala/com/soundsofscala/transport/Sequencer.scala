package com.soundsofscala.transport

import cats.effect.IO
import com.soundsofscala.Main.loadAudioSample
import com.soundsofscala.models.*
import com.soundsofscala.synthesis.ScalaSynth
import org.scalajs.dom
import org.scalajs.dom.AudioContext

enum Sequencer(tempo: Tempo):

  private def playMusicalEvent(
      musicalEvent: MusicalEvent,
      tempo: Tempo,
      instrument: ScalaSynth)(using audioContext: AudioContext): IO[Unit] =
    musicalEvent match
      case Melody(head, tail) =>
        playMusicalEvent(head, tempo, instrument) >> playMusicalEvent(tail, tempo, instrument)
      case Harmony(root, harmony) =>
        IO.println(s"TODO: Harmony started with root: $root and harmony: $harmony")
      case event: AtomicMusicalEvent =>
        event match
          case note: Note =>
//            Sequencer.playASingleNote(note) >>
            Sequencer.playSynth(note, tempo, instrument)
          case Rest(_) => IO.unit
          case DrumStroke(drum, duration, velocity) =>
            IO(println(s"Drum-stroke: $drum, $duration, $velocity"))
  case SingleVoiceSequencer(musicEvent: MusicalEvent, tempo: Tempo)
      extends Sequencer(tempo: Tempo)

  case MultiVoiceSequencer(allVoices: AllVoices, tempo: Tempo) extends Sequencer(tempo: Tempo)
  def play(instrument: ScalaSynth)(using audioContext: AudioContext): IO[Unit] =
    this match
      case SingleVoiceSequencer(musicEvent, tempo) =>
        for {
          _ <- IO(println(s"Playing Music Event"))
          _ <- playMusicalEvent(musicEvent, tempo, instrument)

        } yield {}
      case MultiVoiceSequencer(_, _) => IO.println("MultiVoiceSequencer started")

object Sequencer:
  private def playSynth(note: Note, tempo: Tempo, instrument: ScalaSynth): IO[Unit] =
    IO(println(s"Playing note: $note")) *>
      instrument.attackRelease(note, tempo)

  def playASingleNote(note: Note)(using audioContext: AudioContext): IO[Unit] =
    val filePath = s"resources/audio/piano/${note.pitch}${note.octave.value}.wav"
    IO.println(filePath) >>
      IO(
        loadAudioSample(
          filePath,
          audioContext,
          buffer => {
            val gainNode = audioContext.createGain()
            gainNode.gain.value = 0.5
            val sourceNode = audioContext.createBufferSource()
            sourceNode.buffer = buffer
            sourceNode.connect(gainNode)
            gainNode.connect(audioContext.destination)
//            sourceNode.onended = (_: dom.Event) => println("Playback ended.")
            sourceNode.start()
          }
        ))

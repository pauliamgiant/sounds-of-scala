package com.soundsofscala.transport

import cats.effect.IO
import com.soundsofscala.Main.loadAudioSample
import com.soundsofscala.models.*
import com.soundsofscala.synthesis.ScalaSynth
import org.scalajs.dom
import org.scalajs.dom.AudioContext
import com.soundsofscala.models.AtomicMusicalEvent.*

// this is POC sequencer and to be replaced using the same Lookahead structure as the metronome
// and the SimpleScala808DrumMachine
enum Sequencer(tempo: Tempo):

  private def playMusicalEvent(
      musicalEvent: MusicalEvent,
      tempo: Tempo,
      instrument: ScalaSynth)(using audioContext: AudioContext): IO[Unit] =
    musicalEvent match
      case Sequence(head, tail) =>
        playMusicalEvent(head, tempo, instrument) >> playMusicalEvent(tail, tempo, instrument)
      case event: AtomicMusicalEvent =>
        event match
          case note: Note =>
            Sequencer.playSynth(note, tempo, instrument)
          case Rest(_) => IO.unit
          case DrumStroke(drum, duration, velocity) =>
            IO(println(s"Drum-stroke: $drum, $duration, $velocity"))
          case Harmony(notes, _) => IO(println(s"Harmony: $notes"))
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
            sourceNode.start()
          }
        ))

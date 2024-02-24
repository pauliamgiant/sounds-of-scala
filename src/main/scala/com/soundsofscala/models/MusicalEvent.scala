package com.soundsofscala.models

import cats.data.NonEmptyList
import com.soundsofscala.TransformMusicalEvents.*
import com.soundsofscala.models.Accidental.*
import com.soundsofscala.models.Duration.*
import com.soundsofscala.models.Velocity.*
import com.soundsofscala.models.AtomicMusicalEvent.*

import scala.annotation.{tailrec, targetName}
import scala.concurrent.duration.FiniteDuration

sealed trait MusicalEvent:
  def repeat(repetitions: Int = 2): MusicalEvent = {
    if (repetitions == 1) this
    else {
      @tailrec
      def loop(count: Int, accum: MusicalEvent): MusicalEvent =
        if (count <= 1) accum
        else loop(count - 1, this + accum)

      loop(repetitions - 1, this + this)
    }
  }

  @targetName("combineMusicEvents")
  def +(other: MusicalEvent): MusicalEvent = this match
    case note: Note => Sequence(note, other)
    case thisRest: Rest =>
      other match
        case thatRest: Rest => Sequence(thisRest, thatRest)
        case _ => Sequence(thisRest, other)
    case drum: DrumStroke => Sequence(drum, other)
    case Sequence(head, tail) => Sequence(head, tail + other)
    // fix this with combine
    case chord: Harmony => Sequence(chord, other)

  override def toString: String = this match
    case Sequence(head, tail) => head.printAtomicEvent() + tail.toString
    case event: AtomicMusicalEvent => event.printAtomicEvent()

final case class Sequence(head: AtomicMusicalEvent, tail: MusicalEvent) extends MusicalEvent

enum AtomicMusicalEvent(duration: Duration) extends MusicalEvent:
  def durationToFiniteDuration(tempo: Tempo): FiniteDuration =
    this.duration.toTimeDuration(tempo)
  def durationToSeconds(tempo: Tempo): Double =
    this.duration.toSeconds(tempo)

  def printAtomicEvent(): String = this match
    case Note(pitch, accidental, duration, octave, velocity) =>
      val firstSection =
        s"$pitch${accidentalToString(accidental)}${octave.value}${velocity}_"
      durationToString(duration, firstSection)
    case Rest(duration) => durationToString(duration, "")
    case DrumStroke(drum, duration, velocity) =>
      val firstSection = s"${drumVoiceToString(drum)}${velocity}_"
      durationToString(duration, firstSection)
    case Harmony(notes, duration) =>
      notes.map(_.note.printAtomicEvent()).toList.mkString(" ") + s"_$duration"

  case Note(
      pitch: Pitch,
      accidental: Accidental,
      duration: Duration,
      octave: Octave,
      velocity: Velocity
  ) extends AtomicMusicalEvent(duration)
  case Rest(duration: Duration) extends AtomicMusicalEvent(duration)
  case DrumStroke(
      drum: DrumVoice,
      duration: Duration,
      velocity: Velocity
  ) extends AtomicMusicalEvent(duration)
  case Harmony(notes: NonEmptyList[HarmonyTiming], duration: Duration)
      extends AtomicMusicalEvent(duration)

final case class HarmonyTiming(note: Note, timingOffset: TimingOffset)
//  Add builder methods for Notes
object MusicalEvent:
  def C(octave: Octave = Octave(3)): Note = Note(Pitch.C, Natural, Quarter, octave, OnFull)
  def D(octave: Octave = Octave(3)): Note = Note(Pitch.D, Natural, Quarter, octave, OnFull)
  def E(octave: Octave = Octave(3)): Note = Note(Pitch.E, Natural, Quarter, octave, OnFull)
  def F(octave: Octave = Octave(3)): Note = Note(Pitch.F, Natural, Quarter, octave, OnFull)
  def G(octave: Octave = Octave(3)): Note = Note(Pitch.G, Natural, Quarter, octave, OnFull)
  def A(octave: Octave = Octave(2)): Note = Note(Pitch.A, Natural, Quarter, octave, OnFull)
  def B(octave: Octave = Octave(2)): Note = Note(Pitch.B, Natural, Quarter, octave, OnFull)

package com.soundsofscala.models

import com.soundsofscala.TransformMusicalEvents.*
import com.soundsofscala.models.Accidental.*
import com.soundsofscala.models.Duration.*
import com.soundsofscala.models.Velocity.*

import scala.annotation.targetName
import scala.concurrent.duration.FiniteDuration

sealed trait MusicalEvent:
  @targetName("combineMusicEvents")
  def +(other: MusicalEvent): MusicalEvent = this match
    case note: Note => Melody(note, other)
    case thisRest: Rest =>
      other match
        case thatRest: Rest => Melody(thisRest, thatRest)
        case _ => Melody(thisRest, other)
    case drum: DrumStroke => Melody(drum, other)
    case Melody(head, tail) => Melody(head, tail + other)
    case Harmony(lower, upper) => Harmony(lower, upper + other)

  override def toString: String = this match
    case Melody(head, tail) => head.printAtomicEvent() + tail.toString
    case Harmony(root, harmony) => root.printAtomicEvent() + harmony.toString
    case event: AtomicMusicalEvent => event.printAtomicEvent()

final case class Melody(head: AtomicMusicalEvent, tail: MusicalEvent) extends MusicalEvent
final case class Harmony(root: AtomicMusicalEvent, harmony: MusicalEvent) extends MusicalEvent
sealed trait AtomicMusicalEvent(duration: Duration) extends MusicalEvent:
  def durationToFiniteDuration(tempo: Tempo): FiniteDuration =
    this.duration.toTimeDuration(tempo)

  def printAtomicEvent(): String = this match
    case Note(pitch, accidental, duration, octave, velocity) =>
      val firstSection =
        s"$pitch${accidentalToString(accidental)}${octave.value}${velocity}_"
      durationToString(duration, firstSection)
    case Rest(duration) => durationToString(duration, "")
    case DrumStroke(drum, duration, velocity) =>
      val firstSection = s"${drumVoiceToString(drum)}${velocity}_"
      durationToString(duration, firstSection)

final case class Note(
    pitch: Pitch,
    accidental: Accidental,
    duration: Duration,
    octave: Octave,
    velocity: Velocity
) extends AtomicMusicalEvent(duration)
final case class Rest(duration: Duration) extends AtomicMusicalEvent(duration)
final case class DrumStroke(
    drum: DrumVoice,
    duration: Duration,
    velocity: Velocity
) extends AtomicMusicalEvent(duration)

//  Add builder methods for Notes
object MusicalEvent:
  def C(octave: Octave = Octave(3)): Note = Note(Pitch.C, Natural, Quarter, octave, OnFull)
  def D(octave: Octave = Octave(3)): Note = Note(Pitch.D, Natural, Quarter, octave, OnFull)
  def E(octave: Octave = Octave(3)): Note = Note(Pitch.E, Natural, Quarter, octave, OnFull)
  def F(octave: Octave = Octave(3)): Note = Note(Pitch.F, Natural, Quarter, octave, OnFull)
  def G(octave: Octave = Octave(3)): Note = Note(Pitch.G, Natural, Quarter, octave, OnFull)
  def A(octave: Octave = Octave(2)): Note = Note(Pitch.A, Natural, Quarter, octave, OnFull)
  def B(octave: Octave = Octave(2)): Note = Note(Pitch.B, Natural, Quarter, octave, OnFull)

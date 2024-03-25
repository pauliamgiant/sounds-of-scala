package org.soundsofscala.models

import cats.data.NonEmptyList
import org.soundsofscala.TransformMusicalEvents.*
import AtomicMusicalEvent.*
import Duration.*
import Velocity.*
import org.soundsofscala.models.Duration.{Eighth, Half, Quarter, Sixteenth, ThirtySecond}
import org.soundsofscala.models.Velocity.{
  Assertively,
  Forte,
  Fortissimo,
  Fortississimo,
  Loud,
  Medium,
  MezzoForte,
  MezzoPiano,
  OnFull,
  Pianissimo,
  Pianississimo,
  Piano,
  Soft,
  Softest,
  TheSilentTreatment
}

import scala.annotation.{tailrec, targetName}

sealed trait MusicalEvent:

  def repeat(repetitions: Int): MusicalEvent =
    if (repetitions == 1) this
    else
      @tailrec
      def loop(count: Int, accum: MusicalEvent): MusicalEvent =
        if (count <= 1) accum
        else loop(count - 1, this + accum)
      loop(repetitions - 1, this + this)

  def repeat: MusicalEvent = repeat(2)

  @targetName("repeatMusicEvents")
  def *(repetitions: Int): MusicalEvent = repeat(repetitions)

  @targetName("combineMusicEventsBetweenBars")
  def |(other: MusicalEvent): MusicalEvent =
    this + other

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

enum AtomicMusicalEvent(duration: Duration, velocity: Velocity) extends MusicalEvent:
  def durationToSeconds(tempo: Tempo): Double =
    this.duration.toSeconds(tempo)
  def normalizedVelocity: Double = this.velocity.getNormalisedVelocity
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
  def withVelocity(newVelocity: Velocity): AtomicMusicalEvent = this match
    case note: Note => note.copy(velocity = newVelocity)
    case rest: Rest => rest
    case drum: DrumStroke => drum.copy(velocity = newVelocity)
    case harmony: Harmony =>
      harmony.copy(notes = harmony
        .notes
        .map(harmTiming =>
          harmTiming.copy(note = harmTiming.note.copy(velocity = newVelocity))))
  def withDuration(newDuration: Duration): AtomicMusicalEvent = this match
    case note: Note => note.copy(duration = newDuration)
    case rest: Rest => rest.copy(duration = newDuration)
    case drum: DrumStroke => drum.copy(duration = newDuration)
    case harmony: Harmony => harmony.copy(duration = newDuration)
  // timing
  def whole: AtomicMusicalEvent = this.withDuration(Duration.Whole)
  def half: AtomicMusicalEvent = this.withDuration(Half)
  def quarter: AtomicMusicalEvent = this.withDuration(Quarter)
  def eighth: AtomicMusicalEvent = this.withDuration(Eighth)
  def sixteenth: AtomicMusicalEvent = this.withDuration(Sixteenth)
  def thirtySecond: AtomicMusicalEvent = this.withDuration(ThirtySecond)
  // velocity
  def ppp: AtomicMusicalEvent = this.withVelocity(Pianississimo)
  def pp: AtomicMusicalEvent = this.withVelocity(Pianissimo)
  def p: AtomicMusicalEvent = this.withVelocity(Piano)
  def mp: AtomicMusicalEvent = this.withVelocity(MezzoPiano)
  def mf: AtomicMusicalEvent = this.withVelocity(MezzoForte)
  def f: AtomicMusicalEvent = this.withVelocity(Forte)
  def ff: AtomicMusicalEvent = this.withVelocity(Fortissimo)
  def fff: AtomicMusicalEvent = this.withVelocity(Fortississimo)
  def muted: AtomicMusicalEvent = this.withVelocity(TheSilentTreatment)
  def softest: AtomicMusicalEvent = this.withVelocity(Softest)
  def soft: AtomicMusicalEvent = this.withVelocity(Soft)
  def medium: AtomicMusicalEvent = this.withVelocity(Medium)
  def assertively: AtomicMusicalEvent = this.withVelocity(Assertively)
  def loud: AtomicMusicalEvent = this.withVelocity(Loud)
  def onFull: AtomicMusicalEvent = this.withVelocity(OnFull)

  // pitch

  case Note(
      pitch: Pitch,
      accidental: Accidental,
      duration: Duration,
      octave: Octave,
      velocity: Velocity
  ) extends AtomicMusicalEvent(duration, velocity)
  case Rest(duration: Duration) extends AtomicMusicalEvent(duration, TheSilentTreatment)
  case DrumStroke(
      drum: DrumVoice,
      duration: Duration,
      velocity: Velocity
  ) extends AtomicMusicalEvent(duration, velocity)
  case Harmony(notes: NonEmptyList[HarmonyTiming], duration: Duration)
      extends AtomicMusicalEvent(duration, OnFull)

final case class HarmonyTiming(note: Note, timingOffset: TimingOffset)

/*
 * Copyright 2024 Sounds of Scala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.soundsofscala.models

import cats.data.NonEmptyList
import cats.syntax.all.*
import org.soundsofscala.TransformMusicalEvents.*
import org.soundsofscala.models.Accidental.*
import org.soundsofscala.models.AtomicMusicalEvent.Note
import org.soundsofscala.models.AtomicMusicalEvent.*
import org.soundsofscala.models.Duration.*
import org.soundsofscala.models.Velocity.*

import scala.annotation.tailrec
import scala.annotation.targetName

sealed trait MusicalEvent:
  def reverse(): MusicalEvent =
    @tailrec
    def loop(current: MusicalEvent, accumulator: MusicalEvent): MusicalEvent =
      current match
        case Sequence(head, tail) => loop(tail, Sequence(head, accumulator))
        case event: AtomicMusicalEvent => Sequence(event, accumulator)

    this match
      case Sequence(head, tail) => loop(tail, head)
      case event: AtomicMusicalEvent => event

  def noteCount(): Int =
    @tailrec
    def loop(acc: Int, tail: MusicalEvent): Int =
      tail match
        case Sequence(_, tail) => loop(1 + acc, tail)
        case event: AtomicMusicalEvent =>
          event match
            case Harmony(notes, _) => notes.length
            case _ => acc + 1
    loop(0, this)

  def repeat(repetitions: Int): MusicalEvent =
    if repetitions === 1 then this
    else
      @tailrec
      def loop(count: Int, accum: MusicalEvent): MusicalEvent =
        if count <= 1 then accum
        else loop(count - 1, this + accum)
      loop(repetitions - 1, this + this)

  def repeat: MusicalEvent = repeat(2)

  def loop: MusicalEvent = repeat(128) // Lets see if this 💥

  @targetName("repeatMusicEvents")
  def *(repetitions: Int): MusicalEvent = repeat(repetitions)

  @targetName("combineMusicEventsBetweenBars")
  def |(other: MusicalEvent): MusicalEvent =
    this + other

  @targetName("combineMusicEvents")
  def +(other: MusicalEvent): MusicalEvent =
    val thisReversed = this.reverse()
    @tailrec
    def loop(current: MusicalEvent, accumulator: MusicalEvent): MusicalEvent =
      current match
        case Sequence(head, tail) => loop(tail, Sequence(head, accumulator))
        case event: AtomicMusicalEvent => Sequence(event, accumulator)
    loop(other, thisReversed).reverse()
end MusicalEvent

final case class Sequence(head: AtomicMusicalEvent, tail: MusicalEvent) extends MusicalEvent

object Freq:
  def calculate(pitch: Pitch, accidental: Accidental, octave: Octave): Double =
    val f = pitch.calculateFrequency
    val referenceOctave = 4
    val thisOctave = octave.value - referenceOctave
    val noteOctave = f * Math.pow(2, thisOctave)
    accidental match
      case Sharp => noteOctave * Math.pow(2, 1.0 / 12)
      case Flat => noteOctave / Math.pow(2, 1.0 / 12)
      case Natural => noteOctave

object AtomicMusicalEvent:
  extension (note: Note)
    def sharp: Note =
      note.copy(accidental = Sharp)

    def flat: Note =
      note.copy(accidental = Flat)

    def frequency: Double =
      Freq.calculate(note.pitch, note.accidental, note.octave)

  extension (harmony: Harmony)
    private def updateVelocity(newVelocity: Velocity): Harmony = harmony.copy(notes =
      harmony.notes.map(item => item.copy(note = item.note.withVelocity(newVelocity))))

enum AtomicMusicalEvent(duration: Duration, velocity: Velocity) extends MusicalEvent:

  def durationToSeconds(tempo: Tempo): Double =
    this.duration.toSeconds(tempo)

  def normalizedVelocity: Double = this.velocity.getNormalisedVelocity

  override def toString: String =
    @tailrec
    def loop(current: MusicalEvent, accumulator: String): String = current match
      case Sequence(head, tail) => loop(tail, accumulator + head.printAtomicEvent())
      case event: AtomicMusicalEvent => accumulator + event.printAtomicEvent()
    loop(this, "")

  private def printCondensed(): String = this match
    case Note(pitch, accidental, _, octave, velocity, offset) =>
      s"$pitch${accidentalToString(accidental)}${octave.value}"
    case Rest(_) => "R"
    case DrumStroke(drum, _, velocity) =>
      s"${drumVoiceToString(drum)}$velocity"
    case Harmony(_, _) => "CHORD"

  private def printAtomicEvent(): String = this match
    case Note(pitch, accidental, duration, octave, velocity, offset) =>
      val firstSection =
        s"$pitch${accidentalToString(accidental)}${octave.value}${velocity}_"
      durationToString(duration, firstSection)
    case Rest(duration) => durationToString(duration, "")
    case DrumStroke(drum, duration, velocity) =>
      val firstSection = s"${drumVoiceToString(drum)}${velocity}_"
      durationToString(duration, firstSection)
    case Harmony(notes, duration) =>
      durationToString(
        duration,
        "[" + notes.toList.map(_.note.printCondensed()).mkString(",") + "]")

  def withVelocity(newVelocity: Velocity): AtomicMusicalEvent = this match
    case note: Note => note.copy(velocity = newVelocity)
    case rest: Rest => rest
    case drum: DrumStroke => drum.copy(velocity = newVelocity)
    case harmony: Harmony => harmony.updateVelocity(newVelocity)

  def withDuration(newDuration: Duration): AtomicMusicalEvent = this match
    case note: Note => note.copy(duration = newDuration)
    case rest: Rest => rest.copy(duration = newDuration)
    case drum: DrumStroke => drum.copy(duration = newDuration)
    case harmony: Harmony => harmony.copy(duration = newDuration)

    // timing
  def whole: AtomicMusicalEvent = this.withDuration(Whole)
  def half: AtomicMusicalEvent = this.withDuration(Half)
  def quarter: AtomicMusicalEvent = this.withDuration(Quarter)
  def eighth: AtomicMusicalEvent = this.withDuration(Eighth)
  def sixteenth: AtomicMusicalEvent = this.withDuration(Sixteenth)
  def thirtySecond: AtomicMusicalEvent = this.withDuration(ThirtySecond)
  def halfTriplet: AtomicMusicalEvent = this.withDuration(HalfTriplet)
  def quarterTriplet: AtomicMusicalEvent = this.withDuration(QuarterTriplet)
  def eighthTriplet: AtomicMusicalEvent = this.withDuration(EighthTriplet)
  def sixteenthTriplet: AtomicMusicalEvent = this.withDuration(SixteenthTriplet)
  def thirtySecondTriplet: AtomicMusicalEvent = this.withDuration(ThirtySecondTriplet)
  def wholeDotted: AtomicMusicalEvent = this.withDuration(WholeDotted)
  def halfDotted: AtomicMusicalEvent = this.withDuration(HalfDotted)
  def quarterDotted: AtomicMusicalEvent = this.withDuration(QuarterDotted)
  def eighthDotted: AtomicMusicalEvent = this.withDuration(EighthDotted)
  def sixteenthDotted: AtomicMusicalEvent = this.withDuration(SixteenthDotted)
  def thirtySecondDotted: AtomicMusicalEvent = this.withDuration(ThirtySecondDotted)

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
      velocity: Velocity,
      offset: TimingOffset = TimingOffset(0)
  ) extends AtomicMusicalEvent(duration, velocity)

  case Rest(duration: Duration) extends AtomicMusicalEvent(duration, TheSilentTreatment)
  case DrumStroke(
      drum: DrumVoice,
      duration: Duration,
      velocity: Velocity
  ) extends AtomicMusicalEvent(duration, velocity)
  case Harmony(notes: NonEmptyList[HarmonyTiming], duration: Duration)
      extends AtomicMusicalEvent(duration, Medium)
end AtomicMusicalEvent

final case class HarmonyTiming(note: AtomicMusicalEvent, timingOffset: TimingOffset)
object Chord:
  def apply(
      root: AtomicMusicalEvent,
      parts: AtomicMusicalEvent*
  ): Harmony =
    AtomicMusicalEvent.Harmony(
      NonEmptyList(
        HarmonyTiming(root, TimingOffset(0)),
        parts.map(part => HarmonyTiming(part, TimingOffset(0))).toList
      ),
      root match
        case Note(_, _, duration, _, _, _) => duration
        case Rest(duration) => duration
        case _ => Quarter
    )

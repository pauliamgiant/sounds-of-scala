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
import cats.effect.IO
import org.soundsofscala.models.Accidental.*
import org.soundsofscala.models.AtomicMusicalEvent.*
import org.soundsofscala.models.Duration.*
import org.soundsofscala.models.Velocity.*
import org.soundsofscala.syntax.all.*
import weaver.SimpleIOSuite

import scala.util.Random

object MusicalEventTest extends SimpleIOSuite:

  val c3: Note = Note(Pitch.C, Natural, Quarter, Octave(3), Medium)

  // --- Combine / Sequence building ---

  test("combine appends sequences"):
    val result = Sequence(A2, B2) + Sequence(C3, D3)
    IO.pure(expect.same(result, Sequence(A2, Sequence(B2, Sequence(C3, D3)))))

  test("combine single notes"):
    val result = A2 + B2
    IO.pure(expect.same(result, Sequence(A2, B2)))

  test("combine three notes"):
    val result = A2 + B2 + C3
    IO.pure(expect.same(result, Sequence(A2, Sequence(B2, C3))))

  // --- Repeat ---

  test("repeat(1) returns self"):
    val note: MusicalEvent = C4.medium.quarter
    IO.pure(expect(note.repeat(1).noteCount() == 1))

  test("repeat(4) produces 4 notes"):
    val result = C4.medium.quarter * 4
    IO.pure(expect(result.noteCount() == 4))

  test("repeat(2) is same as repeat"):
    val note: MusicalEvent = A2
    IO.pure(expect.same(note.repeat(2), note.repeat))

  // --- Note count ---

  test("single note has count 1"):
    IO.pure(expect(A2.noteCount() == 1))

  test("sequence of 3 has count 3"):
    val seq = A2 + B2 + C3
    IO.pure(expect(seq.noteCount() == 3))

  test("large song doesn't cause stack overflow"):
    val notes = Seq[MusicalEvent](A1, B1, C1, D1, E1, F1, G1)
    val sizes = List((1000, 1001), (10000, 10001), (50000, 50001))
    IO.pure(
      forEach(sizes): (count, expected) =>
        val testSong = (1 to count).foldLeft[MusicalEvent](C2)((acc, _) =>
          acc.+(notes(Random.nextInt(7))))
        expect(testSong.noteCount() == expected)
    )

  // --- Reverse ---

  test("reverse single note returns itself"):
    IO.pure(expect.same(A2.reverse(), A2))

  test("reverse two-note sequence"):
    IO.pure(expect.same(Sequence(A2, B2).reverse(), Sequence(B2, A2)))

  test("reverse three-note sequence"):
    val seq = Sequence(A2, Sequence(B2, C3))
    IO.pure(expect.same(seq.reverse(), Sequence(C3, Sequence(B2, A2))))

  test("reverse preserves all notes"):
    val seq = A2 + B2 + C3 + D3 + E3 + F3 + G3
    IO.pure(expect.same(seq.reverse().reverse(), seq))

  // --- Sharp / Flat ---

  test("sharp sharpens a note"):
    IO.pure(expect.same(c3.sharp, C3.sharp))

  test("flat flattens a note"):
    IO.pure(expect.same(c3.flat, C3.flat))

  test("sharp sets accidental to Sharp"):
    IO.pure(expect(c3.sharp.accidental == Sharp))

  test("flat sets accidental to Flat"):
    IO.pure(expect(c3.flat.accidental == Flat))

  // --- Duration modifiers ---

  test("duration modifiers change note duration"):
    val note = C4.medium.quarter
    IO.pure(
      expect(note.whole.durationToBeats == 4.0) and
        expect(note.half.durationToBeats == 2.0) and
        expect(note.quarter.durationToBeats == 1.0) and
        expect(note.eighth.durationToBeats == 0.5) and
        expect(note.sixteenth.durationToBeats == 0.25))

  test("dotted durations are 1.5x the base"):
    val note = C4.medium.quarter
    IO.pure(
      expect(note.halfDotted.durationToBeats == 3.0) and
        expect(note.quarterDotted.durationToBeats == 1.5) and
        expect(note.eighthDotted.durationToBeats == 0.75))

  // --- Velocity modifiers ---

  test("velocity modifiers change note velocity"):
    val note = C4.medium.quarter
    IO.pure(
      expect(note.muted.normalizedVelocity == 0.0) and
        expect(note.soft.normalizedVelocity > 0.0) and
        expect(note.medium.normalizedVelocity > note.soft.normalizedVelocity) and
        expect(note.loud.normalizedVelocity > note.medium.normalizedVelocity) and
        expect(note.onFull.normalizedVelocity == 1.0))

  test("classical velocity names map correctly"):
    val note = C4.medium.quarter
    IO.pure(
      expect(note.ppp.normalizedVelocity == note.softest.normalizedVelocity) and
        expect(note.ff.normalizedVelocity == note.loud.normalizedVelocity) and
        expect(note.fff.normalizedVelocity == note.onFull.normalizedVelocity))

  // --- Rest ---

  test("rest has correct duration"):
    IO.pure(
      expect(Rest(Quarter).durationToBeats == 1.0) and
        expect(Rest(Eighth).durationToBeats == 0.5) and
        expect(Rest(Whole).durationToBeats == 4.0))

  test("rest has zero velocity"):
    IO.pure(expect(Rest(Quarter).normalizedVelocity == 0.0))

  // --- Chords ---

  test("chord contains all notes"):
    IO.pure(
      expect(Cmaj.noteCount() == 3) and
        expect.same(
          Cmaj.notes,
          NonEmptyList(
            HarmonyTiming(C3, TimingOffset(0)),
            List(HarmonyTiming(E3, TimingOffset(0)), HarmonyTiming(G3, TimingOffset(0))))))

  test("chord contains correct notes for Dmaj"):
    IO.pure(
      expect(Dmaj.noteCount() == 3) and
        expect.same(
          Dmaj.notes,
          NonEmptyList(
            HarmonyTiming(D3, TimingOffset(0)),
            List(
              HarmonyTiming(F3.sharp, TimingOffset(0)),
              HarmonyTiming(A3, TimingOffset(0))))))

  test("4 note chord contains correct notes"):
    IO.pure(
      expect(Cmaj7.noteCount() == 4) and
        expect.same(
          Cmaj7.notes,
          NonEmptyList(
            HarmonyTiming(C3, TimingOffset(0)),
            List(
              HarmonyTiming(E3, TimingOffset(0)),
              HarmonyTiming(G3, TimingOffset(0)),
              HarmonyTiming(B3, TimingOffset(0))))
        ))

  test("5 note chord contains correct notes"):
    IO.pure(
      expect(Cmaj9.noteCount() == 5) and
        expect.same(
          Cmaj9.notes,
          NonEmptyList(
            HarmonyTiming(C3, TimingOffset(0)),
            List(
              HarmonyTiming(E3, TimingOffset(0)),
              HarmonyTiming(G3, TimingOffset(0)),
              HarmonyTiming(B3, TimingOffset(0)),
              HarmonyTiming(D4, TimingOffset(0)))
          )
        ))

  test("minor chord has flat third"):
    IO.pure(
      expect(Cmin.noteCount() == 3) and
        expect.same(
          Cmin.notes,
          NonEmptyList(
            HarmonyTiming(C3, TimingOffset(0)),
            List(
              HarmonyTiming(E3.flat, TimingOffset(0)),
              HarmonyTiming(G3, TimingOffset(0))))))

  test("chord duration matches root note duration"):
    val chord = Chord(C3.half, E3, G3)
    IO.pure(expect(chord.durationToBeats == 2.0))

  // --- DrumStroke ---

  test("drum stroke has correct duration"):
    val kick = DrumStroke(DrumVoice.Kick, Quarter, OnFull)
    IO.pure(expect(kick.durationToBeats == 1.0))

  test("drum stroke velocity can be changed"):
    val kick = DrumStroke(DrumVoice.Kick, Quarter, OnFull)
    IO.pure(
      expect(kick.normalizedVelocity == 1.0) and
        expect(kick.soft.normalizedVelocity < 1.0))

  // --- Pipe operator ---

  test("pipe operator combines same as +"):
    val a = A2 + B2
    val b = A2 | B2
    IO.pure(expect.same(a, b))

  // --- durationToSeconds ---

  test("durationToSeconds at 60 BPM"):
    IO.pure(
      expect(C4.medium.quarter.durationToSeconds(Tempo(60)) == 1.0) and
        expect(C4.medium.half.durationToSeconds(Tempo(60)) == 2.0) and
        expect(C4.medium.eighth.durationToSeconds(Tempo(60)) == 0.5))

  test("durationToSeconds at 120 BPM"):
    IO.pure(
      expect(C4.medium.quarter.durationToSeconds(Tempo(120)) == 0.5) and
        expect(C4.medium.half.durationToSeconds(Tempo(120)) == 1.0) and
        expect(C4.medium.whole.durationToSeconds(Tempo(120)) == 2.0))
end MusicalEventTest

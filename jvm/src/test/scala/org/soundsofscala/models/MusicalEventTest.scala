package org.soundsofscala.models

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.soundsofscala.models.AtomicMusicalEvent.Note
import org.soundsofscala.syntax.all.*
import org.soundsofscala.models.Accidental.*
import org.soundsofscala.models.Duration.*
import org.soundsofscala.models.Octave.*
import org.soundsofscala.models.Velocity.*
import cats.data.NonEmptyList

import scala.util.Random

class MusicalEventTest extends AnyFunSuite with Matchers with TableDrivenPropertyChecks:

  test("Large song doesn't cause a stack overflow"):
    val notes = Seq[MusicalEvent](A1, B1, C1, D1, E1, F1, G1)
    Table(
      ("size", "count"),
      (1000, 1001),
      (10000, 10001),
      (50000, 50001)
    ).forEvery: (count, expected) =>
      val testSong =
        (1 to count).foldLeft[MusicalEvent](C2)((acc, _) => acc.+(notes(Random.nextInt(7))))
      testSong.noteCount() shouldBe expected

  test("testCombineMethod"):
    Table(
      ("sequence1", "sequence2", "expected"),
      (Sequence(A2, B2), Sequence(C3, D3), Sequence(A2, Sequence(B2, Sequence(C3, D3))))
    ).forEvery: (sequence1, sequence2, expected) =>
      sequence1 + sequence2 shouldBe expected

  test("Sharp sharpens a Note"):
    Table(
      ("note", "sharpened"),
      (Note(Pitch.C, Natural, Quarter, Octave(3), Medium), C3.sharp)
    ).forEvery: (note, sharpened) =>
      println(sharpened)
      note.sharp shouldBe sharpened

  test("Flat flattens a Note"):
    Table(
      ("note", "flat"),
      (Note(Pitch.C, Natural, Quarter, Octave(3), Medium), C3.flat)
    ).forEvery: (note, flattened) =>
      println(flattened)
      note.flat shouldBe flattened

  test("Chord contains all notes"):
    println("Chord")
    Cmaj.noteCount() shouldBe 3
    Cmaj.notes shouldBe NonEmptyList(
      HarmonyTiming(C3, TimingOffset(0)),
      List(HarmonyTiming(E3, TimingOffset(0)), HarmonyTiming(G3, TimingOffset(0))))

  test("Chord contains correct notes"):
    println("Chord")
    Dmaj.noteCount() shouldBe 3
    Dmaj.notes shouldBe NonEmptyList(
      HarmonyTiming(D3, TimingOffset(0)),
      List(HarmonyTiming(F3.sharp, TimingOffset(0)), HarmonyTiming(A3, TimingOffset(0))))

  test("4 note Chord contains correct notes"):
    println("Chord")
    Cmaj7.noteCount() shouldBe 4
    Cmaj7.notes shouldBe NonEmptyList(
      HarmonyTiming(C3, TimingOffset(0)),
      List(
        HarmonyTiming(E3, TimingOffset(0)),
        HarmonyTiming(G3, TimingOffset(0)),
        HarmonyTiming(B3, TimingOffset(0))))

  test("5 note Chord contains correct notes"):
    println("Chord")
    Cmaj9.noteCount() shouldBe 5
    Cmaj9.notes shouldBe NonEmptyList(
      HarmonyTiming(C3, TimingOffset(0)),
      List(
        HarmonyTiming(E3, TimingOffset(0)),
        HarmonyTiming(G3, TimingOffset(0)),
        HarmonyTiming(B3, TimingOffset(0)),
        HarmonyTiming(D4, TimingOffset(0)))
    )

  test("testReverseNote"):
    Table(
      ("sequence", "expected"),
      (Sequence(A2, B2), Sequence(B2, A2)),
      (Sequence(A2, Sequence(B2, C3)), Sequence(C3, Sequence(B2, A2))),
      (
        Sequence(A2, Sequence(B2, Sequence(C3, D3))),
        Sequence(D3, Sequence(C3, Sequence(B2, A2)))),
      (
        Sequence(A2, Sequence(B2, Sequence(C3, Sequence(D3, E3)))),
        Sequence(E3, Sequence(D3, Sequence(C3, Sequence(B2, A2))))),
      (
        Sequence(A2, Sequence(B2, Sequence(C3, Sequence(D3, Sequence(E3, F3))))),
        Sequence(F3, Sequence(E3, Sequence(D3, Sequence(C3, Sequence(B2, A2)))))),
      (
        Sequence(A2, Sequence(B2, Sequence(C3, Sequence(D3, Sequence(E3, Sequence(F3, G3)))))),
        Sequence(G3, Sequence(F3, Sequence(E3, Sequence(D3, Sequence(C3, Sequence(B2, A2)))))))
    ).forEvery: (sequence, expected) =>
      sequence.reverse() shouldBe expected

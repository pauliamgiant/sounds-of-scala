package org.soundsofscala.models

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.soundsofscala.syntax.all.*
import scala.util.Random

class MusicalEventTest extends AnyFunSuite with Matchers with TableDrivenPropertyChecks:

  test("testCombineMethod"):
    Table(
      ("sequence1", "sequence2", "expected"),
      (Sequence(A2, B2), Sequence(C3, D3), Sequence(A2, Sequence(B2, Sequence(C3, D3))))
    ).forEvery: (sequence1, sequence2, expected) =>
      sequence1 + sequence2 shouldBe expected

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

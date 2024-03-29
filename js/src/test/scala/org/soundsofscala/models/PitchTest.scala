package org.soundsofscala.models

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.soundsofscala.models.Pitch.*

class PitchTest extends AnyFunSuite with Matchers with TableDrivenPropertyChecks:

  test("Test Calculate Frequency"):

    Table(
      ("pitch", "frequency"),
      (A, 440),
      (B, 493.883),
      (C, 261.626),
      (D, 293.665),
      (E, 329.628),
      (F, 349.228),
      (G, 391.995)
    ).forEvery((pitch, frequency) => pitch.calculateFrequency shouldBe frequency)

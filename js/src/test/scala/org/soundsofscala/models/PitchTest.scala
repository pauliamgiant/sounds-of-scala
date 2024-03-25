package org.soundsofscala.models

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.soundsofscala.models.Pitch.{A, B}

class PitchTest extends AnyFunSuite with Matchers {

  test("Test Calculate Frequency") {

    A.calculateFrequency shouldBe 440
    B.calculateFrequency shouldBe 493.883
  }
}

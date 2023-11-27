package com.soundsofscala.models
import com.soundsofscala.models.DrumVoice.{HiHatClosed, *}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import com.soundsofscala.models.Duration.*
import com.soundsofscala.models.Accidental.*
import com.soundsofscala.models.MusicalEvent.*

import org.scalatest.funsuite.AnyFunSuite

class MusicalEventTest extends AnyFunSuite with Matchers {

  test("testCombineMethod") {
    1 + 1 shouldBe 2
  }

  test("testPrintEvent") {}

}

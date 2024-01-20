package com.soundsofscala.models

import com.soundsofscala.models.Duration.*
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks

import scala.concurrent.duration.*

class DurationTest extends AnyFunSuite with Matchers with TableDrivenPropertyChecks {

  test("test duration to millisecond conversion") {
    val table = Table(
      ("note", "tempo", "expected"),
      (Whole, Tempo(60), 4000.milliseconds),
      (Half, Tempo(60), 2000.milliseconds),
      (Quarter, Tempo(60), 1000.milliseconds),
      (Eighth, Tempo(60), 500.milliseconds),
      (Sixteenth, Tempo(60), 250.milliseconds),
      (ThirtySecond, Tempo(60), 125.milliseconds),
      (SixtyFourth, Tempo(60), 63.milliseconds),
      (Whole, Tempo(112), 2143.milliseconds),
      (Half, Tempo(117), 1026.milliseconds),
      (Quarter, Tempo(120), 500.milliseconds),
      (Eighth, Tempo(123), 244.milliseconds),
      (Sixteenth, Tempo(126), 119.milliseconds)
    )
    forAll(table) { (note, tempo, milliseconds) =>
      note.toTimeDuration(tempo) shouldBe milliseconds
    }
  }

}

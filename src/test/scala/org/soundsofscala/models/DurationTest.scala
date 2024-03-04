package org.soundsofscala.models

import Duration.*
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.soundsofscala.models.Duration.{
  Eighth,
  Half,
  Quarter,
  Sixteenth,
  SixtyFourth,
  ThirtySecond,
  Whole
}

import scala.concurrent.duration.*

class DurationTest extends AnyFunSuite with Matchers with TableDrivenPropertyChecks {

  test("test duration to millisecond conversion") {
    val table = Table(
      ("note", "tempo", "expected"),
      (Whole, Tempo(60), 4),
      (Half, Tempo(60), 2),
      (Quarter, Tempo(60), 1),
      (Eighth, Tempo(60), .5),
      (Sixteenth, Tempo(60), .250),
      (ThirtySecond, Tempo(60), 0.125),
      (SixtyFourth, Tempo(60), 0.0625),
      (Whole, Tempo(112), 2.142857142857143),
      (Half, Tempo(117), 1.0256410256410255),
      (Quarter, Tempo(120), .5),
      (Eighth, Tempo(123), 0.24390243902439024),
      (Sixteenth, Tempo(126), 0.11904761904761904)
    )
    forAll(table)((note, tempo, seconds) => note.toSeconds(tempo) shouldBe seconds)
  }

}

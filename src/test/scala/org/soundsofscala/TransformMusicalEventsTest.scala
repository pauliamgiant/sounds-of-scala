package org.soundsofscala

import org.soundsofscala.models.DrumVoice.{HiHatClosed, *}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.soundsofscala.models.Duration.*
import org.soundsofscala.models.Accidental.*
import org.scalatest.prop.TableDrivenPropertyChecks
import org.soundsofscala.models.Accidental.{Flat, Natural, Sharp}
import org.soundsofscala.models.DrumVoice.{
  Clap,
  Cowbell,
  Crash,
  HiHatClosed,
  HiHatOpen,
  Kick,
  Ride,
  Rimshot,
  Snare,
  Tambourine,
  TomHigh,
  TomLow,
  TomMid
}
import org.soundsofscala.models.Duration.{
  Eighth,
  Half,
  Quarter,
  Sixteenth,
  SixtyFourth,
  ThirtySecond,
  Whole
}

class TransformMusicalEventsTest
    extends AnyFunSuite
    with Matchers
    with TableDrivenPropertyChecks {

  test("testDrumVoiceToString") {
    Table(
      ("drumVoice", "expected"),
      (Kick, "Doob"),
      (Snare, "Crack"),
      (HiHatClosed, "Tsst"),
      (HiHatOpen, "Tssssss"),
      (Crash, "Pshhhh"),
      (Ride, "Ting"),
      (TomHigh, "Dim"),
      (TomMid, "Dum"),
      (TomLow, "Duh"),
      (Rimshot, "Tock"),
      (Clap, "Clap"),
      (Cowbell, "Ding"),
      (Tambourine, "Chinka")
    ).forEvery { (drumVoice, expected) =>
      TransformMusicalEvents.drumVoiceToString(drumVoice) shouldBe expected
    }
  }

  test("testDurationToString") {
    Table(
      ("duration", "expected"),
      (Whole, 64),
      (Half, 32),
      (Quarter, 16),
      (Eighth, 8),
      (Sixteenth, 4),
      (ThirtySecond, 2),
      (SixtyFourth, 1)
    ).forEvery { (duration, expected) =>
      TransformMusicalEvents.durationToString(duration, "").length shouldBe expected
    }
  }

  test("testAccidentalToString") {
    Table(
      ("accidental", "expected"),
      (Sharp, "#"),
      (Flat, "♭"),
      (Natural, "")
    ).forEvery { (accidental, expected) =>
      TransformMusicalEvents.accidentalToString(accidental) shouldBe expected
    }
  }

}

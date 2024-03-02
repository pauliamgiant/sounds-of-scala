package com.soundsofscala

import com.soundsofscala.models.*
import com.soundsofscala.models.Velocity.*
import com.soundsofscala.models.Accidental.*
import com.soundsofscala.models.AtomicMusicalEvent.*
import com.soundsofscala.models.DrumVoice.*
import com.soundsofscala.models.Duration.*

package object syntax {
  object all {

    // start of DSL
    def C(octave: Octave): Note = Note(Pitch.C, Natural, Quarter, octave, OnFull)

    def D(octave: Octave): Note = Note(Pitch.D, Natural, Quarter, octave, OnFull)

    def E(octave: Octave): Note = Note(Pitch.E, Natural, Quarter, octave, OnFull)

    def F(octave: Octave): Note = Note(Pitch.F, Natural, Quarter, octave, OnFull)

    def G(octave: Octave): Note = Note(Pitch.G, Natural, Quarter, octave, OnFull)

    def A(octave: Octave): Note = Note(Pitch.A, Natural, Quarter, octave, OnFull)

    def B(octave: Octave): Note = Note(Pitch.B, Natural, Quarter, octave, OnFull)

    val `A-2`: Note = A(Octave(-2))
    val `B-2`: Note = B(Octave(-2))
    val `C-2`: Note = C(Octave(-2))
    val `D-2`: Note = D(Octave(-2))
    val `E-2`: Note = E(Octave(-2))
    val `F-2`: Note = F(Octave(-2))
    val `G-2`: Note = G(Octave(-2))

    val `A-1`: Note = A(Octave(-1))
    val `B-1`: Note = B(Octave(-1))
    val `C-1`: Note = C(Octave(-1))
    val `D-1`: Note = D(Octave(-1))
    val `E-1`: Note = E(Octave(-1))
    val `F-1`: Note = F(Octave(-1))
    val `G-1`: Note = G(Octave(-1))

    val A0: Note = A(Octave(0))
    val B0: Note = B(Octave(0))
    val C0: Note = C(Octave(0))
    val D0: Note = D(Octave(0))
    val E0: Note = E(Octave(0))
    val F0: Note = F(Octave(0))
    val G0: Note = G(Octave(0))

    val A1: Note = A(Octave(1))
    val B1: Note = B(Octave(1))
    val C1: Note = C(Octave(1))
    val D1: Note = D(Octave(1))
    val E1: Note = E(Octave(1))
    val F1: Note = F(Octave(1))
    val G1: Note = G(Octave(1))

    val A2: Note = A(Octave(2))
    val B2: Note = B(Octave(2))
    val C2: Note = C(Octave(2))
    val D2: Note = D(Octave(2))
    val E2: Note = E(Octave(2))
    val F2: Note = F(Octave(2))
    val G2: Note = G(Octave(2))

    val A3: Note = A(Octave(3))
    val B3: Note = B(Octave(3))
    val C3: Note = C(Octave(3))
    val D3: Note = D(Octave(3))
    val E3: Note = E(Octave(3))
    val F3: Note = F(Octave(3))
    val G3: Note = G(Octave(3))

    val A4: Note = A(Octave(3))
    val B4: Note = B(Octave(3))
    val C4: Note = C(Octave(3))
    val D4: Note = D(Octave(3))
    val E4: Note = E(Octave(3))
    val F4: Note = F(Octave(3))
    val G4: Note = G(Octave(3))

    val A5: Note = A(Octave(5))
    val B5: Note = B(Octave(5))
    val C5: Note = C(Octave(5))
    val D5: Note = D(Octave(5))
    val E5: Note = E(Octave(5))
    val F5: Note = F(Octave(5))
    val G5: Note = G(Octave(5))

    val A6: Note = A(Octave(6))
    val B6: Note = B(Octave(6))
    val C6: Note = C(Octave(6))
    val D6: Note = D(Octave(6))
    val E6: Note = E(Octave(6))
    val F6: Note = F(Octave(6))
    val G6: Note = G(Octave(6))

    val A7: Note = A(Octave(7))
    val B7: Note = B(Octave(7))
    val C7: Note = C(Octave(7))
    val D7: Note = D(Octave(7))
    val E7: Note = E(Octave(7))
    val F7: Note = F(Octave(7))
    val G7: Note = G(Octave(7))

    val A8: Note = A(Octave(8))
    val B8: Note = B(Octave(8))
    val C8: Note = C(Octave(8))
    val D8: Note = D(Octave(8))
    val E8: Note = E(Octave(8))
    val F8: Note = F(Octave(8))
    val G8: Note = G(Octave(8))

    val RestWhole: Rest = Rest(Duration.Whole)
    val RestHalf: Rest = Rest(Half)
    val RestQuarter: Rest = Rest(Quarter)
    val RestEighth: Rest = Rest(Eighth)
    val RestSixteenth: Rest = Rest(Sixteenth)
    val RestThirtySecondth: Rest = Rest(ThirtySecond)
    val OneBarRest: MusicalEvent =
      RestWhole
    val TwoBarRest: MusicalEvent = OneBarRest + OneBarRest
    val FourBarRest: MusicalEvent = TwoBarRest + TwoBarRest
    val EightBarRest: MusicalEvent = FourBarRest + FourBarRest

  }
}

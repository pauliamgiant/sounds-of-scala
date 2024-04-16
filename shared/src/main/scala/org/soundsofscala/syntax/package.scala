package org.soundsofscala

import cats.data.NonEmptyList
import org.soundsofscala.models.Accidental.Natural
import org.soundsofscala.models.AtomicMusicalEvent.*
import org.soundsofscala.models.Duration.*
import org.soundsofscala.models.Velocity.*
import org.soundsofscala.models.*
import org.soundsofscala.models.DrumVoice.*

package object syntax:
  object all:

    // start of DSL
    def C(octave: Octave): Note = Note(Pitch.C, Natural, Quarter, octave, Medium)

    def D(octave: Octave): Note = Note(Pitch.D, Natural, Quarter, octave, Medium)

    def E(octave: Octave): Note = Note(Pitch.E, Natural, Quarter, octave, Medium)

    def F(octave: Octave): Note = Note(Pitch.F, Natural, Quarter, octave, Medium)

    def G(octave: Octave): Note = Note(Pitch.G, Natural, Quarter, octave, Medium)

    def A(octave: Octave): Note = Note(Pitch.A, Natural, Quarter, octave, Medium)

    def B(octave: Octave): Note = Note(Pitch.B, Natural, Quarter, octave, Medium)

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

    val A4: Note = A(Octave(4))
    val B4: Note = B(Octave(4))
    val C4: Note = C(Octave(4))
    val D4: Note = D(Octave(4))
    val E4: Note = E(Octave(4))
    val F4: Note = F(Octave(4))
    val G4: Note = G(Octave(4))

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

    val RestWhole: Rest = Rest(Whole)
    val RestHalf: Rest = Rest(Half)
    val RestQuarter: Rest = Rest(Quarter)
    val RestEighth: Rest = Rest(Eighth)
    val RestSixteenth: Rest = Rest(Sixteenth)
    val RestThirtySecondth: Rest = Rest(ThirtySecond)
    val OneBarRest: MusicalEvent = RestWhole
    val TwoBarRest: MusicalEvent = OneBarRest + OneBarRest
    val FourBarRest: MusicalEvent = TwoBarRest + TwoBarRest
    val EightBarRest: MusicalEvent = FourBarRest + FourBarRest

    val r1: MusicalEvent = RestWhole
    val r2: MusicalEvent = RestHalf
    val r4: MusicalEvent = RestQuarter
    val r8: MusicalEvent = RestEighth
    val r16: MusicalEvent = RestSixteenth
    val r32: MusicalEvent = RestThirtySecondth

    // chords

    val Cmaj: Harmony = Chord(C3, E3, G3)
    val Cmaj7: Harmony = Chord(C3, E3, G3, B3)
    val Cmaj9: Harmony = Chord(C3, E3, G3, B3, D4)
    val Cmaj11: Harmony = Chord(C3, E3, G3, B3, D4, F4)
    val Cmaj13: Harmony = Chord(C3, E3, G3, B3, D4, F4, A4)

    val Dmaj: Harmony = Chord(D3, F3.sharp, A3)
    val Dmaj7: Harmony = Chord(D3, F3.sharp, A3, C4.sharp)
    val Dmaj9: Harmony = Chord(D3, F3.sharp, A3, C4.sharp, E4)
    val Dmaj11: Harmony = Chord(D3, F3.sharp, A3, C4.sharp, E4, G4)
    val Dmaj13: Harmony = Chord(D3, F3.sharp, A3, C4.sharp, E4, G4, B4)

    val Emaj: Harmony = Chord(E3, G3.sharp, B3)
    val Emaj7: Harmony = Chord(E3, G3.sharp, B3, D4.sharp)
    val Emaj9: Harmony = Chord(E3, G3.sharp, B3, D4.sharp, F4.sharp)
    val Emaj11: Harmony =
      Chord(E3, G3.sharp, B3, D4.sharp, F4.sharp, A4.sharp)
    val Emaj13: Harmony =
      Chord(E3, G3.sharp, B3, D4.sharp, F4.sharp, A4.sharp, C5.sharp)

    val Fmaj: Harmony = Chord(F3, A3, C4)
    val Fmaj7: Harmony = Chord(F3, A3, C4, E4)
    val Fmaj9: Harmony = Chord(F3, A3, C4, E4, G4)
    val Fmaj11: Harmony = Chord(F3, A3, C4, E4, G4, B4.flat)
    val Fmaj13: Harmony = Chord(F3, A3, C4, E4, G4, B4.flat, D5)

    val Gmaj: Harmony = Chord(G3, B3, D4)
    val Gmaj7: Harmony = Chord(G3, B3, D4, F4.sharp)
    val Gmaj9: Harmony = Chord(G3, B3, D4, F4.sharp, A4)
    val Gmaj11: Harmony = Chord(G3, B3, D4, F4.sharp, A4, C5)
    val Gmaj13: Harmony = Chord(G3, B3, D4, F4.sharp, A4, C5, E5)

    val Amaj: Harmony = Chord(A3, C4.sharp, E4)
    val Amaj7: Harmony = Chord(A3, C4.sharp, E4, G4.sharp)
    val Amaj9: Harmony = Chord(A3, C4.sharp, E4, G4.sharp, B4)
    val Amaj11: Harmony = Chord(A3, C4.sharp, E4, G4.sharp, B4, D5)
    val Amaj13: Harmony = Chord(A3, C4.sharp, E4, G4.sharp, B4, D5, F5.sharp)

    val Bmaj: Harmony = Chord(B3, D4.sharp, F4.sharp)
    val Bmaj7: Harmony = Chord(B3, D4.sharp, F4.sharp, A4.sharp)
    val Bmaj9: Harmony = Chord(B3, D4.sharp, F4.sharp, A4.sharp, C5.sharp)
    val Bmaj11: Harmony =
      Chord(B3, D4.sharp, F4.sharp, A4.sharp, C5.sharp, E5.sharp)
    val Bmaj13: Harmony =
      Chord(B3, D4.sharp, F4.sharp, A4.sharp, C5.sharp, E5.sharp, G5.sharp)

    val Cmin: Harmony = Chord(C3, E3.flat, G3)
    val Cmin7: Harmony = Chord(C3, E3.flat, G3, B3.flat)
    val Cmin9: Harmony = Chord(C3, E3.flat, G3, B3.flat, D4)
    val Cmin11: Harmony = Chord(C3, E3.flat, G3, B3.flat, D4, F4)
    val Cmin13: Harmony = Chord(C3, E3.flat, G3, B3.flat, D4, F4, A4.flat)

    val Dmin: Harmony = Chord(D3, F3, A3)
    val Dmin7: Harmony = Chord(D3, F3, A3, C4)
    val Dmin9: Harmony = Chord(D3, F3, A3, C4, E4)
    val Dmin11: Harmony = Chord(D3, F3, A3, C4, E4, G4)
    val Dmin13: Harmony = Chord(D3, F3, A3, C4, E4, G4, B4.flat)

    val Emin: Harmony = Chord(E3, G3, B3)
    val Emin7: Harmony = Chord(E3, G3, B3, D4)
    val Emin9: Harmony = Chord(E3, G3, B3, D4, F4.sharp)
    val Emin11: Harmony = Chord(E3, G3, B3, D4, F4.sharp, A4)
    val Emin13: Harmony = Chord(E3, G3, B3, D4, F4.sharp, A4, C5)

    val Fmin: Harmony = Chord(F3, A3.flat, C4)
    val Fmin7: Harmony = Chord(F3, A3.flat, C4, E4.flat)
    val Fmin9: Harmony = Chord(F3, A3.flat, C4, E4.flat, G4)
    val Fmin11: Harmony = Chord(F3, A3.flat, C4, E4.flat, G4, B4.flat)
    val Fmin13: Harmony =
      Chord(F3, A3.flat, C4, E4.flat, G4, B4.flat, D5.flat)

    val Gmin: Harmony = Chord(G3, B3.flat, D4)
    val Gmin7: Harmony = Chord(G3, B3.flat, D4, F4)
    val Gmin9: Harmony = Chord(G3, B3.flat, D4, F4, A4)
    val Gmin11: Harmony = Chord(G3, B3.flat, D4, F4, A4, C5)
    val Gmin13: Harmony = Chord(G3, B3.flat, D4, F4, A4, C5, E5.flat)

    val Amin: Harmony = Chord(A3, C4, E4)
    val Amin7: Harmony = Chord(A3, C4, E4, G4)
    val Amin9: Harmony = Chord(A3, C4, E4, G4, B4)
    val Amin11: Harmony = Chord(A3, C4, E4, G4, B4, D5)
    val Amin13: Harmony = Chord(A3, C4, E4, G4, B4, D5, F5)

    val Bmin: Harmony = Chord(B3, D4, F4.sharp)
    val Bmin7: Harmony = Chord(B3, D4, F4.sharp, A4)
    val Bmin9: Harmony = Chord(B3, D4, F4.sharp, A4, C5.sharp)
    val Bmin11: Harmony = Chord(B3, D4, F4.sharp, A4, C5.sharp, E5)
    val Bmin13: Harmony = Chord(B3, D4, F4.sharp, A4, C5.sharp, E5, G5)

// drums

    val kk = DrumStroke(Kick, Quarter, Medium)
    val sn = DrumStroke(Snare, Quarter, Medium)
    val hhc = DrumStroke(HiHatClosed, Quarter, Medium)
    val hho = DrumStroke(HiHatOpen, Quarter, Medium)
    val cr = DrumStroke(Crash, Quarter, Medium)
    val rd = DrumStroke(Ride, Quarter, Medium)
    val t1 = DrumStroke(TomHigh, Quarter, Medium)
    val t2 = DrumStroke(TomMid, Quarter, Medium)
    val ft = DrumStroke(FloorTom, Quarter, Medium)
    val clp = DrumStroke(Clap, Quarter, Loud)
    val KickDrum: AtomicMusicalEvent = kk
    val SnareDrum: AtomicMusicalEvent = sn
    val HatsClosed: AtomicMusicalEvent = hhc
    val HatsOpen: AtomicMusicalEvent = hho
    val CrashCymbal: AtomicMusicalEvent = cr
    val RideCymbal: AtomicMusicalEvent = rd
    val Tom1: AtomicMusicalEvent = t1
    val Tom2: AtomicMusicalEvent = t2
    val Tom3: AtomicMusicalEvent = ft
    val HandClap: AtomicMusicalEvent = clp
  end all

  /*

# Back in Black Drum Beat

## Introduction
Tempo: 94 BPM
Time Signature: 4/4

1 e & a | 2 e & a | 3 e & a | 4 e & a |
---------------------------------------
Bass Drum | x       | x       | x       |
Snare     | x   x   | x   x   | x   x   |
Hi-Hat    | x x x x | x x x x | x x x x |
   */
end syntax

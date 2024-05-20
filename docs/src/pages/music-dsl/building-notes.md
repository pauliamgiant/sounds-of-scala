### Building Notes

Notes are the building blocks of music. They are defined by their pitch, accidental, duration, octave, velocity and offset.

Here is the Note Model
```scala 3
enum AtomoicMusicalEvent:
  case Note(
    pitch: Pitch,
    accidental: Accidental,
    duration: Duration,
    octave: Octave,
    velocity: Velocity,
    offset: TimingOffset = TimingOffset(0)
  ) extends AtomicMusicalEvent(duration, velocity)
```
### Properties of a Note
- **pitch**: The pitch of the note (A, B, C, D, E, F, G)
- **accidental**: The accidental of the note (Natural, Sharp, Flat)
- **duration**: The duration of the note (Whole, Half, Quarter, Eighth, Sixteenth, ThirtySecond)
- **octave**: The octave of the note (Octave1, Octave2, Octave3, Octave4, Octave5, Octave6, Octave7)
- **velocity**: The velocity of the note (Soft, Medium, Loud) or (pp, p, mp, mf, f, ff)
- **offset**: The timing offset of the note to gain further fine grained control of the timing of the note (yet to be implemented)

Here is an example of defining a single note:
```scala 3
Note(C, Natural, Quarter, Octave4, Medium)
```

### Idiomatic Notes
Again, we have a range of syntactic sugar to make instantiating a note more readable:
```scala 3
C4.sharp.eighth.soft
```
### [Next Step: Building Melodies](../music-dsl/building-melodies.md)

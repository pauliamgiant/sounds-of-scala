### Building Notes

Notes are the building blocks of music. They are defined by their pitch, accidental, duration, octave, velocity and offset.

Here is the Note Model
```scala 3
enum AtomicMusicalEvent:
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
- **octave**: The octave of the note, a refined `Int` constructed as `Octave(n)` where n is typically 0 to 8
- **velocity**: The velocity of the note. Plain English names: `Softest`, `Soft`, `Medium`, `Assertively`, `Loud`, `Louder`, `OnFull`. Musical dynamics: `Pianississimo`, `Pianissimo`, `Piano`, `MezzoPiano`, `MezzoForte`, `Forte`, `Fortissimo`, `Fortississimo`. Shorthand methods are also available on notes: `.ppp`, `.pp`, `.p`, `.mp`, `.mf`, `.f`, `.ff`, `.fff`
- **offset**: The timing offset of the note for fine grained control of timing

Here is an example of defining a single note:
```scala 3
Note(C, Natural, Quarter, Octave(4), Medium)
```

### Idiomatic Notes
Again, we have a range of syntactic sugar to make instantiating a note more readable:
```scala 3
C4.sharp.eighth.soft
```
### [Next Step: Building Melodies](../music-dsl/building-melodies.md)

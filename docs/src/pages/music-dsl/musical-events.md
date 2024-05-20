### The MusicalEvent Type

This is the base trait and highest level type for all musical events.
There are two main subtypes of `MusicalEvent`:
- `AtomicMusicalEvent` - a single event that can be played
- `Sequence` - a sequence of events that can be played in order

```scala 3
sealed trait MusicalEvent:
    enum AtomicMusicalEvent(duration: Duration, velocity: Velocity) extends MusicalEvent
    final case class Sequence(head: AtomicMusicalEvent, tail: MusicalEvent) extends MusicalEvent
```


### Types of AtomicMusicalEvents

- `Note` - a single note
- `Rest` - a pause in the music
- `DrumStroke` - a single drum stroke
- `Harmony` - a chord or notes in the same voice played at the same time

### [Next Step: Building Drum Beats](../music-dsl/building-beats.md)


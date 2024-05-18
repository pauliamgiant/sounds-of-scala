## The Song Type

The Song type is the top level object containing all elements required to play a song.

```scala 3
case class Song(
    title: Title,
    tempo: Tempo = Tempo(120),
    swing: Swing = Swing(0),
    mixer: Mixer
)
```
The title, tempo and swing, should be self explanatory and take provided types wrapping a string and integers for these values.

> NOTE: Swing is yet to be implemented.

### The Mixer Type

Mixer is where we we will place all our **Tracks** and this will simulate the effect of a real world mixing desk, where we can simultaneously play and manipulate audio on multiple tracks.


```scala 3
case class Mixer(tracks: NonEmptyList[Track])
```

> NOTE: The **NonEmptyList** type is a type from the [cats library](https://typelevel.org/cats/datatypes/nel.html) which ensures that the list is never empty.

> [What is a mixing desk?](https://en.wikipedia.org/wiki/Mixing_console)

### The Track Type

Tracks in turn, are where we will place:
- The musical events we want to play.
- The instruments we want to play those events with.
- Any insert FX we want to apply to the track. [Insert FX](https://en.wikipedia.org/wiki/Insert_(effects_processing))
- Any send FX we want to apply to the track. [Send FX](https://en.wikipedia.org/wiki/Aux-send)

```scala 3
case class Track(
                  title: Title,
                  musicalEvent: MusicalEvent,
                  instrument: Instrument,
                  insertFX: List[FX] = List.empty,
                  sendFX: List[FX] = List.empty)
```

### [Next Step: Musical Events](http://localhost:4242/music-dsl/musical-events.html)
## The Song Type

The Song type is the top level data structure containing all elements required to define a song. Song is pure data — it holds no mutable state and has no side effects.

```scala 3
case class Song(
    title: Title,
    tempo: Tempo = Tempo(120),
    swing: Swing = Swing(SwingAmount(0), SwingResolution.Eighth),
    mixer: Mixer
)
```
The title, tempo and swing, should be self explanatory and take provided types wrapping a string and integers for these values.

`Swing` takes a `SwingAmount` (a refined `Int`) and a `SwingResolution` (`Eighth` or `Sixteenth`) that controls which beats get the swing feel.

### The Mixer Type

Mixer is where we will place all our **Tracks** and this will simulate the effect of a real world mixing desk, where we can simultaneously play and manipulate audio on multiple tracks.


```scala 3
case class Mixer(tracks: NonEmptyList[Track[?]])
```

> NOTE: The **NonEmptyList** type is a type from the [cats library](https://typelevel.org/cats/datatypes/nel.html) which ensures that the list is never empty.

> [What is a mixing desk?](https://en.wikipedia.org/wiki/Mixing_console)

### The Track Type

Tracks in turn, are where we will place:
- The musical events we want to play.
- The instruments we want to play those events with.
- The playback mode — `Playback.Loop` for continuously looping tracks (e.g. drums) or `Playback.OneShot` for tracks that play once and stop (e.g. arranged parts).
- TODO: Any insert FX we want to apply to the track. [Insert FX](https://en.wikipedia.org/wiki/Insert_(effects_processing))
- TODO: Any send FX we want to apply to the track. [Send FX](https://en.wikipedia.org/wiki/Aux-send)

```scala 3
case class Track[Settings](
    title: Title,
    musicalEvent: MusicalEvent,
    instrument: Instrument[Settings],
    playback: Playback,
    customSettings: Option[Settings] = None,
    insertFX: List[FX] = List.empty,
    sendFX: List[FX] = List.empty)(using Default[Settings])
```

### Playing a Song with the Sequencer

Song is pure data — to play it, wrap it in a `Ref[IO, Song]` and pass it to a `Sequencer`:

```scala 3
for
  songRef   <- Ref.of[IO, Song](mySong)
  sequencer <- Sequencer(songRef)
  _         <- sequencer.play()
yield ()
```

The `Ref` enables real-time updates to any song property while it's playing:

```scala 3
sequencer.updateSong(_.copy(tempo = Tempo(140)))
```

### [Next Step: Musical Events](../music-dsl/musical-events.md)

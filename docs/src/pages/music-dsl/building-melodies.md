## Building Melodies

Melodies are a sequence of notes. They can be defined using the Sequence class. We have already seen the sequence class in the [building beats](http://localhost:4242/music-dsl/building-beats.html) section.


```scala 3
  val musicalEvent: MusicalEvent =
    C3 + C3 + G3 + G3 + A3 + A3 + G3.half |
    F3 + F3 + E3 + E3 + D3 + D3 + C3.half
```

To listen to this you can import ExampleSong0 from the `sounds-of-scala` library and play it.

### [Next Step: Syntax](http://localhost:4242/music-dsl/syntax.html)

### PianoSynth


The PianoSynth is a synthesised keyboard instrument built using the Web Audio API's wavetable oscillator. It generates its tone from a custom Fourier series with six harmonics, giving it a bright, clear timbre. A velocity sensitive envelope shapes each note with a fast attack, a gentle decay, and a long exponential release.

We're confident one day it will sound more like an electronic piano.. until then it can limp along in this role.

#### Creating a PianoSynth

The PianoSynth is created as an `IO` since it initialises internal state for tracking active audio nodes.

```scala 3
val piano: IO[PianoSynth] = PianoSynth()
```

#### Using in a Song

A single PianoSynth instance can be shared across multiple tracks. Here is an example from the Beethoven song included with the library:

```scala 3
PianoSynth().map: sharedPiano =>
  Song(
    title = Title("Something We All Know"),
    tempo = Tempo(110),
    mixer = Mixer(
      Track(
        Title("Upper Voice"),
        upperVoice,
        sharedPiano,
        Playback.OneShot),
      Track(
        Title("Lower Voice"),
        lowerVoice,
        sharedPiano,
        Playback.OneShot)
    ))
```



### [Next Step: ScalaSynth](../instruments/ScalaSynth.md)

### Sampler

This Sampler was built as a Scala Centre Google summer of code project in 2024 by Johanna Odersky. It acts like a classic hardward sampler in a traditional sense, allowing you to load real audio samples and play them back as part of a song at any pitch.

Here is a link to [Johanna's project write up](https://gist.github.com/ikukojohanna/5e23c4bd79e24c2aeb8454b2507981d1).

When a note is played, it finds the closest sample to the target frequency using a binary search and adjusts the playback rate to match the requested pitch. This keeps pitch shifting artefacts to a minimum by always playing from the nearest available sample.

#### Built in Sample Sets

The library ships with several ready to use sample sets:

```scala 3
Sampler.piano        // Piano samples across 2 octaves
Sampler.guitar       // Acoustic guitar samples played by Harvey Cambridge potentially in his bedroom.
Sampler.bassGuitar   // Pauls musicman stingray bass guitar samples across 2 octaves
Sampler.kickDrum     // Doob, Doob
Sampler.snareDrum    // Snare drum
Sampler.rhubarb      // Yup, rhubarb
Sampler.vinyl        // Vinyl noise
Sampler.sparkles     // Sparkle effect
```

#### Loading your own Samples

You can create a Sampler from any audio files by providing a list of `SampleKey` to file path pairs. Each `SampleKey` identifies the pitch of the sample so the Sampler knows how to pitch shift from it.

```scala 3
val mySampler: IO[Sampler] = Sampler.fromPaths(List(
  SampleKey(Pitch.C, Accidental.Natural, Octave(3)) -> "path/to/c3.wav",
  SampleKey(Pitch.G, Accidental.Natural, Octave(3)) -> "path/to/g3.wav"
))
```

The more samples you provide across the pitch range, the less pitch shifting is needed and the more natural the result will sound.

The best examples of how to use the Sampler can be found in the `Sampler` companion object itself. Each built in sample set (such as `Sampler.piano` or `Sampler.bassGuitar`) demonstrates the pattern: define a list of `SampleKey` to file path pairs, then call `Sampler.fromPaths`. The companion object source is at `org.soundsofscala.instrument.Sampler`.

#### Example Songs using the Sampler

`ExampleSong1` is a full multi track song that mixes samplers with synths. It creates a custom drum sampler from individual kick, snare and hihat audio files, loads the built in `Sampler.bassGuitar` and `Sampler.guitar`, and combines them with `ScalaSynth` and `QuirkyFilterSynth` tracks. It is a good reference for how to wire up custom `SamplePlayer.Settings` per track.

`ExampleSongSampler` showcases the more unusual built in sample sets like `Sampler.rhubarb`, `Sampler.vinyl` and `Sampler.sparkles`. It also demonstrates reversed playback using custom `SamplePlayer.Settings` with `reversed = true`, along with offset and length controls for slicing into a sample.

Both can be found in the `org.soundsofscala.songexamples` package.

### [Next Step: PianoSynth](../instruments/PianoSynth.md)

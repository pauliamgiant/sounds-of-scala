# Instruments

Every track in a song needs an instrument to produce sound. All instruments implement the `Instrument` trait, which provides `play` and `stop` methods. The library ships with synthesizers, drum machines and a sampler.

#### Sampler

The **Sampler** is one of the most powerful instruments in the library. It lets you load real audio samples and play them back at any pitch by automatically adjusting the playback rate. It uses a binary search to find the closest sample to the target frequency, keeping pitch shifting artefacts to a minimum.

The library ships with several built in sample sets ready to use: `Sampler.piano`, `Sampler.guitar`, `Sampler.bassGuitar`, `Sampler.kickDrum`, `Sampler.snareDrum` and a few more unusual ones. You can also load your own samples from any audio file path using `Sampler.fromPaths`.

#### Synthesizers

Synths generate sound from oscillators and are used for melodic content (notes, chords, melodies). Each synth uses a different waveform or synthesis technique to produce its timbre:

- **ScalaSynth** - The default synth, a simple starting point
- **PianoSynth** - Wavetable oscillator with a velocity sensitive envelope. There is a built in Piano sampler however that uses real samples so if you actually want to play a piano you should use that instead.
- **ViolinSynth** - Wavetable oscillator with vibrato and filtering to approximate a bowed string
- **QuirkyFilterSynth** - An experimental synth with filter modulation for more unusual timbres. If anybody calls you quirky it will surely be a compliment.

All synths extend the `Synth` trait, which handles note and chord playback. You can call `Synth()` to get the default (ScalaSynth).

#### Drum Machines

- **Simple80sDrumMachine** - A digital drum synthesiser modelled on vintage 80s electronic drum machines with Kick, Snare and HiHat voices

### [Next Step: Sampler](../instruments/Sampler.md)



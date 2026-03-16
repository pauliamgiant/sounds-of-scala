## Quick Song Example

Lets just dive in here with a Song example before explaining the components involved.


```scala 3
def firstMusicProgram(): AudioContext ?=> IO[Unit] =
  for
    song      <- writingAFirstSong()
    songRef   <- Ref.of[IO, Song](song)
    sequencer <- Sequencer(songRef)
    _         <- sequencer.play()
  yield ()

def writingAFirstSong(): AudioContext ?=> IO[Song] =

  // define musical events - here we define a softly played C major chord repeated 4 times
  val cMajorFourTimes: MusicalEvent = Cmaj.soft * 4

  for
    scalaSynth  <- ScalaSynth()
    drumMachine <- Simple80sDrumMachine()
  yield Song(
    title = Title("First, Maybe Last Song"),
    tempo = Tempo(120),
    swing = Swing(0),
    mixer = Mixer(
      Track(
        title = Title("Scala Synth Bassline"),
        musicalEvent = cMajorFourTimes,
        instrument = scalaSynth,
        playback = Playback.OneShot),
      Track(
        title = Title("Pulsing Kick Drum"),
        musicalEvent = KickDrum.eighth.loud * 8,
        instrument = drumMachine,
        playback = Playback.Loop)
    )
  )
```

### [Next Step: The Song type](../music-dsl/songs.md)


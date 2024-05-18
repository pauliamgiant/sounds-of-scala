## Quick Song Example

Lets just dive in here with a Song example before explaining the components involved.


```scala 3
def firstMusicProgram(): AudioContext ?=> IO[Unit] =
  // TODO: Your code
  writingAFirstSong().play()  // play the below song

def writingAFirstSong(): AudioContext ?=> Song =

  // define musical events - here we define a softly played C major chord repeated 4 times
  val cMajorFourTimes: MusicalEvent = Cmaj.soft * 4

  // pass the musical events into a track, alongside a track title and an instrument to play the musical events with
  val track1 = Track(
    title = Title("Scala Synth Bassline"),
    musicalEvent = cMajorFourTimes,
    instrument = ScalaSynth())

  val track2 = Track(
    title = Title("Pulsing Kick Drum"),
    musicalEvent = KickDrum.eighth.loud * 8,
    instrument = SimpleDrumMachine())

  // pass the tracks to the mixer
  val mixer = Mixer(track1, track2)

  // pass the mixer alongside title, tempo and swing into the song
  Song(Title("First, Maybe Last Song"), Tempo(120), Swing(0), mixer)
)
```

### [Next Step: The Song type](http://localhost:4242/music-dsl/songs.html)


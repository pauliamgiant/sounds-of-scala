# Playing a Test Song

Playing one of the example songs included with the library is a good way to check that you have everything configured correctly.

To play one of these songs first import the following:

```scala
import org.soundsofscala.models.Song
import org.soundsofscala.songs.*
```

Before we can produce any sounds from our browser, the first components we need in any Web Audio project in an AudioContext.

The AudioContext is the fundamental component in the Web Audio API for controlling the execution of Audio.

[Read about the AudioContext](https://developer.mozilla.org/en-US/docs/Web/API/AudioContext "AudioContext")

All audio components in the Sounds of Scala library need an implicit/given AudioContext. We can define a given AudiContext as follows:

```scala
given AudioContext = new dom.AudioContext()
```

Now using the **firstMusicProgram** method from the example project on the previous page a simple song can be defined as follows:

```scala 3
def firstMusicProgram(): AudioContext ?=> IO[Unit] =
  // TODO: Your code
  ExampleSong1().play()

```

Once you have played that and heard some Audio playing you can:
- Click into `ExampleSong1` and see how the song is defined
- Try changing the song to play one of the other example songs:
    - `ExampleSong2ChromaticScale`
    - `ExampleSong3Chords`
    - `ExampleSong4Beethoven`
    - `ExampleDrumBeat1`

### [Next Step: Creating your own Song](../music-dsl/songs.md#the-song-type)
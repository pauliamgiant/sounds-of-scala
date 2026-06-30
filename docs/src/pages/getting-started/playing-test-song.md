# Playing a Test Song

Playing one of the example songs included with the library is a good way to check that you have everything configured correctly.

To play one of these songs first import the following:

```scala 3
import cats.effect.{IO, Ref}
import org.scalajs.dom.AudioContext
import org.soundsofscala.models.*
import org.soundsofscala.songexamples.*
import org.soundsofscala.transport.Sequencer
```

Before we can produce any sounds from our browser, the first component we need in any Web Audio project is an AudioContext.

The AudioContext is the fundamental component in the Web Audio API for controlling the execution of Audio.

[Read about the AudioContext](https://developer.mozilla.org/en-US/docs/Web/API/AudioContext "AudioContext")

All audio components in the Sounds of Scala library need an implicit/given AudioContext. We can define a given AudioContext as follows:

```scala 3
given AudioContext = new AudioContext()
```

Now using the **firstMusicProgram** method from the example project on the previous page a simple song can be played as follows:

```scala 3
def firstMusicProgram(): AudioContext ?=> IO[Unit] =
  for
    song      <- ExampleSong1.song()
    songRef   <- Ref.of[IO, Song](song)
    sequencer <- Sequencer(songRef)
    _         <- sequencer.play()
  yield ()
```

The `Song` is pure data. It holds the title, tempo, swing and mixer (tracks). To play it, we wrap it in a `Ref[IO, Song]` and pass it to a `Sequencer`.

#### Why a Ref?

The Sequencer re-reads the `Song` from the `Ref` before scheduling every note. This means any change you make to the `Song` (tempo, swing, or even swapping out an entire track's pattern) takes effect immediately during playback. Without the `Ref`, the Sequencer would have a static snapshot of the song with no way to react to changes.

This is also what enables pause and resume. The Sequencer tracks the current beat position as it plays, so when you call `sequencer.pause()` it preserves where you are in the song, and `sequencer.play()` picks up from that position.

Once you have played that and heard some audio playing you can:
- Click into `ExampleSong1` and see how the song is defined
- Try changing the song to play one of the other example songs:
    - `ExampleSong2`
    - `ExampleSong3`
    - `ExampleSong4`
    - `ExampleSong5Beethoven`
    - `ExampleSong6`

### [Next Step: Creating your own Song](../music-dsl/songs.md#the-song-type)

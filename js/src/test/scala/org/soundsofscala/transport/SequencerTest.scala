/*
 * Copyright 2024 Sounds of Scala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.soundsofscala.transport

import cats.data.NonEmptyList
import cats.effect.IO
import cats.effect.Ref
import org.scalajs.dom.AudioContext
import org.soundsofscala.instrument.Default.NoSettings
import org.soundsofscala.instrument.NoInstrument
import org.soundsofscala.models.*
import org.soundsofscala.syntax.all.*
import weaver.SimpleIOSuite
import org.soundsofscala.TestUtils

object SequencerTest extends SimpleIOSuite:

  given AudioContext = TestUtils.stubAudioContext

  private val testEvent: MusicalEvent = C4.medium.quarter

  private def testSong(tempo: Tempo = Tempo(120)): Song =
    Song(
      title = Title("Test Song"),
      tempo = tempo,
      mixer = Mixer(NonEmptyList.one(
        Track(Title("Track 1"), testEvent, NoInstrument[NoSettings](), Playback.OneShot)
      ))
    )

  private def makeSequencer(
      song: Song = testSong()): IO[(Sequencer, Ref[IO, Song])] =
    for
      songRef <- Ref.of[IO, Song](song)
      sequencer <- Sequencer(songRef)
    yield (sequencer, songRef)

  test("updateSong modifies the song in the Ref"):
    makeSequencer().flatMap: (sequencer, songRef) =>
      for
        _ <- sequencer.updateSong(_.copy(tempo = Tempo(140)))
        song <- songRef.get
      yield expect.eql(song.tempo.value, 140.0)

  test("currentBeatPosition starts at zero"):
    makeSequencer().flatMap: (sequencer, _) =>
      sequencer.currentBeatPosition.map: beatPosition =>
        expect.eql(beatPosition.value, 0.0)

  test("toggleClick toggles mute state"):
    makeSequencer().flatMap: (sequencer, _) =>
      for
        first <- sequencer.toggleClickOnOff
        second <- sequencer.toggleClickOnOff
      yield expect.eql(first, false) and expect.eql(second, true)

  test("stop is a no-op when not playing"):
    makeSequencer().flatMap: (sequencer, _) =>
      sequencer.stop().map(_ => success)

  test("pause captures current beat position"):
    makeSequencer().flatMap: (sequencer, _) =>
      for
        _ <- sequencer.pause()
        beatPosition <- sequencer.currentBeatPosition
      yield expect.eql(beatPosition.value, 0.0)

  test("stop after pause resets beat position"):
    makeSequencer().flatMap: (sequencer, _) =>
      for
        _ <- sequencer.pause()
        _ <- sequencer.stop()
        beatPosition <- sequencer.currentBeatPosition
      yield expect.eql(beatPosition.value, 0.0)
end SequencerTest

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
import cats.syntax.all.*
import org.scalajs.dom.AudioContext
import org.soundsofscala.TestUtils
import org.soundsofscala.instrument.Default.NoSettings
import org.soundsofscala.instrument.NoInstrument
import org.soundsofscala.models.*
import org.soundsofscala.models.AtomicMusicalEvent.Rest
import org.soundsofscala.models.Duration.*
import org.soundsofscala.models.Track.resolveTrack
import org.soundsofscala.syntax.all.*
import weaver.SimpleIOSuite

object NoteSchedulerTest extends SimpleIOSuite:

  given AudioContext = TestUtils.stubAudioContext

  private val tempo120 = Tempo(120) // quarter note = 0.5s
  private val tempo60 = Tempo(60) // quarter note = 1.0s
  private val noSwing = Swing(SwingAmount(0), SwingResolution.Eighth)
  private val scheduleWindow = ScheduleWindow(10.0)
  private val lookAhead = LookAhead(25)

  private val clickTrackEvent: MusicalEvent = (C4.medium.sixteenth + Rest(Sixteenth)) * 8
  private val clickTrackTrack: Track[NoSettings] =
    Track(Title("Click"), clickTrackEvent, NoInstrument[NoSettings](), Playback.Loop)

  private def makeSong(event: MusicalEvent, tempo: Tempo = tempo120, swing: Swing = noSwing): Song =
    Song(
      title = Title("Test"),
      tempo = tempo,
      swing = swing,
      mixer = Mixer(NonEmptyList.one(
        Track(Title("Track 1"), event, NoInstrument[NoSettings](), Playback.OneShot)
      ))
    )

  private def trackFor(trackIndex: TrackIndex, song: Song): Track[?] =
    trackIndex.resolveTrack(song, clickTrackTrack)

  private def makeScheduler(song: Song): IO[(NoteScheduler, Ref[IO, Song], Ref[IO, BeatPosition])] =
    for
      songRef <- Ref.of[IO, Song](song)
      beatPositionRef <- Ref.of[IO, BeatPosition](BeatPosition(0.0))
      scheduler =
        NoteScheduler(songRef, beatPositionRef, lookAhead, scheduleWindow, clickTrackTrack)
    yield (scheduler, songRef, beatPositionRef)

  // --- Basic scheduling ---

  test("single quarter note at 120 BPM returns correct NextNoteTime"):
    val song = makeSong(C4.medium.quarter)
    makeScheduler(song).flatMap: (scheduler, _, _) =>
      scheduler
        .scheduleTrackSequence(
          TrackIndex(1),
          trackFor(TrackIndex(1), song),
          NextNoteTime(0.0),
          none,
          absoluteBeatPositionBase = 0.0)
        .map(result => expect.eql(result.value, 0.5))

  test("single quarter note at 60 BPM returns correct NextNoteTime"):
    val song = makeSong(C4.medium.quarter, tempo = tempo60)
    makeScheduler(song).flatMap: (scheduler, _, _) =>
      scheduler
        .scheduleTrackSequence(
          TrackIndex(1),
          trackFor(TrackIndex(1), song),
          NextNoteTime(0.0),
          none,
          absoluteBeatPositionBase = 0.0)
        .map(result => expect.eql(result.value, 1.0))

  test("sequence of 4 quarter notes at 120 BPM returns NextNoteTime of 2.0"):
    val song = makeSong(C4.medium.quarter * 4)
    makeScheduler(song).flatMap: (scheduler, _, _) =>
      scheduler
        .scheduleTrackSequence(
          TrackIndex(1),
          trackFor(TrackIndex(1), song),
          NextNoteTime(0.0),
          none,
          absoluteBeatPositionBase = 0.0)
        .map(result => expect.eql(result.value, 2.0))

  test("sequence of 8 eighth notes at 120 BPM returns NextNoteTime of 2.0"):
    val song = makeSong(C4.medium.eighth * 8)
    makeScheduler(song).flatMap: (scheduler, _, _) =>
      scheduler
        .scheduleTrackSequence(
          TrackIndex(1),
          trackFor(TrackIndex(1), song),
          NextNoteTime(0.0),
          none,
          absoluteBeatPositionBase = 0.0)
        .map(result => expect.eql(result.value, 2.0))

  test("mixed durations sum correctly"):
    val event = C4.medium.half + D4.medium.quarter + E4.medium.eighth
    val song = makeSong(event)
    makeScheduler(song).flatMap: (scheduler, _, _) =>
      scheduler
        .scheduleTrackSequence(
          TrackIndex(1),
          trackFor(TrackIndex(1), song),
          NextNoteTime(0.0),
          none,
          absoluteBeatPositionBase = 0.0)
        .map(result => expect.eql(result.value, 1.75))

  test("rest notes are scheduled with correct timing"):
    val event = Rest(Quarter) + C4.medium.quarter
    val song = makeSong(event)
    makeScheduler(song).flatMap: (scheduler, _, _) =>
      scheduler
        .scheduleTrackSequence(
          TrackIndex(1),
          trackFor(TrackIndex(1), song),
          NextNoteTime(0.0),
          none,
          absoluteBeatPositionBase = 0.0)
        .map(result => expect.eql(result.value, 1.0))

  test("non-zero start time offsets the result"):
    val song = makeSong(C4.medium.quarter)
    makeScheduler(song).flatMap: (scheduler, _, _) =>
      scheduler
        .scheduleTrackSequence(
          TrackIndex(1),
          trackFor(TrackIndex(1), song),
          NextNoteTime(5.0),
          none,
          absoluteBeatPositionBase = 0.0)
        .map(result => expect.eql(result.value, 5.5))

  // --- Beat position tracking ---

  test("beat position is updated for click track (index 0)"):
    val song = makeSong(C4.medium.quarter * 4)
    makeScheduler(song).flatMap: (scheduler, _, beatPositionRef) =>
      scheduler
        .scheduleTrackSequence(
          TrackIndex(0),
          trackFor(TrackIndex(0), song),
          NextNoteTime(0.0),
          none,
          absoluteBeatPositionBase = 0.0)
        .flatMap(_ => beatPositionRef.get)
        .map(beatPosition => expect.eql(beatPosition.value, 4.0))

  test("beat position is NOT updated for non-click tracks"):
    val song = makeSong(C4.medium.quarter * 4)
    makeScheduler(song).flatMap: (scheduler, _, beatPositionRef) =>
      scheduler
        .scheduleTrackSequence(
          TrackIndex(1),
          trackFor(TrackIndex(1), song),
          NextNoteTime(0.0),
          none,
          absoluteBeatPositionBase = 0.0)
        .flatMap(_ => beatPositionRef.get)
        .map(beatPosition => expect.eql(beatPosition.value, 0.0))

  test("beat position includes absoluteBeatPositionBase for click track"):
    val song = makeSong(C4.medium.quarter * 2)
    makeScheduler(song).flatMap: (scheduler, _, beatPositionRef) =>
      scheduler
        .scheduleTrackSequence(
          TrackIndex(0),
          trackFor(TrackIndex(0), song),
          NextNoteTime(0.0),
          none,
          absoluteBeatPositionBase = 8.0)
        .flatMap(_ => beatPositionRef.get)
        .map(beatPosition => expect.eql(beatPosition.value, 12.0))

  // --- Live updates ---

  test("live update picks up changed pattern mid-sequence"):
    val originalEvent = C4.medium.quarter * 4
    val updatedEvent = C4.medium.eighth * 4
    val song = makeSong(originalEvent)
    makeScheduler(song).flatMap: (scheduler, songRef, _) =>
      songRef.set(makeSong(updatedEvent)) >>
        scheduler
          .scheduleTrackSequence(
            TrackIndex(1),
            trackFor(TrackIndex(1), song),
            NextNoteTime(0.0),
            none,
            absoluteBeatPositionBase = 0.0)
          .map(result => expect.eql(result.value, 1.0))

  // --- Resume from beat position ---

  test("resume from beat position 2.0 skips first two quarter notes"):
    val song = makeSong(C4.medium.quarter * 4)
    makeScheduler(song).flatMap: (scheduler, _, _) =>
      scheduler
        .scheduleTrackSequence(
          TrackIndex(1),
          trackFor(TrackIndex(1), song),
          NextNoteTime(0.0),
          startingBeatPosition = BeatPosition(2.0).some,
          absoluteBeatPositionBase = 0.0
        )
        .map(result => expect.eql(result.value, 1.0))

  test("resume from beat position 1.0 with eighth notes"):
    val song = makeSong(C4.medium.eighth * 8)
    makeScheduler(song).flatMap: (scheduler, _, _) =>
      scheduler
        .scheduleTrackSequence(
          TrackIndex(1),
          trackFor(TrackIndex(1), song),
          NextNoteTime(0.0),
          startingBeatPosition = BeatPosition(1.0).some,
          absoluteBeatPositionBase = 0.0
        )
        .map(result => expect.eql(result.value, 1.5))

  // --- Swing ---

  test("swing offsets are applied to swung beats"):
    val withSwing = Swing(SwingAmount(5), SwingResolution.Eighth)
    val event = C4.medium.eighth * 4
    val songNoSwing = makeSong(event, swing = noSwing)
    val songWithSwing = makeSong(event, swing = withSwing)

    for
      (schedulerNoSwing, _, _) <- makeScheduler(songNoSwing)
      (schedulerWithSwing, _, _) <- makeScheduler(songWithSwing)
      noSwingTime <- schedulerNoSwing
        .scheduleTrackSequence(
          TrackIndex(1),
          trackFor(TrackIndex(1), songNoSwing),
          NextNoteTime(0.0),
          none,
          absoluteBeatPositionBase = 0.0)
      swingTime <- schedulerWithSwing
        .scheduleTrackSequence(
          TrackIndex(1),
          trackFor(TrackIndex(1), songWithSwing),
          NextNoteTime(0.0),
          none,
          absoluteBeatPositionBase = 0.0)
    yield expect.eql(noSwingTime.value, 1.0) and
      expect.eql(swingTime.value, 1.0)

  // --- Tempo ---

  test("tempo change between notes is picked up"):
    val song = makeSong(C4.medium.quarter * 2, tempo = tempo120)
    makeScheduler(song).flatMap: (scheduler, _, _) =>
      scheduler
        .scheduleTrackSequence(
          TrackIndex(1),
          trackFor(TrackIndex(1), song),
          NextNoteTime(0.0),
          none,
          absoluteBeatPositionBase = 0.0)
        .map(result => expect.eql(result.value, 1.0))

  test("slower tempo produces longer schedule time"):
    val song = makeSong(C4.medium.quarter * 2, tempo = tempo60)
    makeScheduler(song).flatMap: (scheduler, _, _) =>
      scheduler
        .scheduleTrackSequence(
          TrackIndex(1),
          trackFor(TrackIndex(1), song),
          NextNoteTime(0.0),
          none,
          absoluteBeatPositionBase = 0.0)
        .map(result => expect.eql(result.value, 2.0))

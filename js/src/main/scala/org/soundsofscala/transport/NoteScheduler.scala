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

import cats.effect.IO
import cats.effect.Ref
import org.scalajs.dom.AudioContext
import org.soundsofscala.models.*

import scala.annotation.tailrec
import scala.concurrent.duration.DurationDouble

/**
 * The `NoteScheduler` is responsible for scheduling the notes of a single track. It reads the
 * current Song from a `Ref[IO, Song]`, extracting the track by index. Tempo is re-read per-note for
 * live tempo changes. The track's pattern, instrument, and playback mode are re-read at each cycle
 * boundary.
 *
 * @param songRef
 *   A Ref holding the current Song, re-read for live updates
 * @param lookAheadMs
 *   The time in milliseconds to look ahead to determine if the next note should be scheduled
 * @param scheduleAheadTimeSeconds
 *   The time window in seconds in which to schedule notes ahead of time
 */

private case class PlaybackLocation(
    locatedEvent: MusicalEvent,
    locatedBeatPosition: BeatPosition,
    adjustedNoteTime: NextNoteTime)

private case class ScheduleContext(
    trackIndex: TrackIndex,
    initialEvent: MusicalEvent,
    remainingEvent: MusicalEvent,
    nextNoteTime: NextNoteTime,
    beatPosition: BeatPosition,
    startingBeatPosition: Option[BeatPosition],
    swingCompensation: SwingOffset,
    beatPositionOffset: Double = 0.0
)

final case class NoteScheduler(
    songRef: Ref[IO, Song],
    beatPositionRef: Ref[IO, BeatPosition],
    lookAheadMs: LookAhead,
    scheduleAheadTimeSeconds: ScheduleWindow,
    clickTrack: Track[?]):

  private def resolveTrack(trackIndex: TrackIndex, song: Song): Track[?] =
    if trackIndex.value == 0 then clickTrack
    else song.mixer.tracks.toList(trackIndex.value - 1)

  def scheduleTrack(
      trackIndex: TrackIndex,
      startingBeatPosition: Option[BeatPosition] = None
  )(using ac: AudioContext): IO[Unit] =
    def playCycle(
        nextNoteTime: NextNoteTime,
        beatOffset: Double,
        resumePosition: Option[BeatPosition]
    ): IO[Unit] =
      for
        song <- songRef.get
        track = resolveTrack(trackIndex, song)
        patternLength = totalDurationInBeats(track.musicalEvent)
        localResume = resumePosition.map(bp => BeatPosition(bp.value % patternLength))
        adjustedOffset = resumePosition.fold(beatOffset)(bp =>
          bp.value - (bp.value % patternLength))
        scheduleContext = ScheduleContext(
          trackIndex,
          initialEvent = track.musicalEvent,
          remainingEvent = track.musicalEvent,
          nextNoteTime,
          BeatPosition(0.0),
          localResume,
          SwingOffset(0.0),
          beatPositionOffset = adjustedOffset
        )
        finalNoteTime <- scheduleSequence(scheduleContext)
        _ <-
          track.playback match
            case Playback.Loop =>
              playCycle(finalNoteTime, adjustedOffset + patternLength, resumePosition = None)
            case Playback.OneShot => IO.unit
      yield ()

    playCycle(NextNoteTime(ac.currentTime), beatOffset = 0.0, resumePosition = startingBeatPosition)
  end scheduleTrack

  private def scheduleSequence(context: ScheduleContext)(using AudioContext): IO[NextNoteTime] =
    ScheduleStatus.isReadyToSchedule(context.nextNoteTime, scheduleAheadTimeSeconds) match
      case ScheduleStatus.Waiting =>
        IO.sleep(lookAheadMs.value.millis) >> scheduleSequence(context)
      case ScheduleStatus.Ready =>
        // read latest version of the song
        songRef.get.flatMap: song =>
          val track = resolveTrack(context.trackIndex, song)
          val currentEventFromSongRef = track.musicalEvent

          // this compares the event from the song ref with the original event specified when the song started playing
          if currentEventFromSongRef != context.initialEvent
          // If changed then updated sequence and call this scheduleSequence method again
          then applyLiveUpdate(context, currentEventFromSongRef, song)
          else playNextNote(context, song)

  private def playNextNote(context: ScheduleContext, song: Song)(
      using AudioContext): IO[NextNoteTime] =
    val track = resolveTrack(context.trackIndex, song)
    val swingInMillis = calculateSwingOffset(song)

    def calculateNextNoteTime(
        baseTime: NextNoteTime,
        note: AtomicMusicalEvent,
        swingOffset: Double): NextNoteTime =
      NextNoteTime(
        baseTime.value + swingOffset + context.swingCompensation.value +
          note
            .durationToSeconds(song.tempo))

    val playBackLocation = locatePlaybackPosition(context, song.tempo)
    playBackLocation.locatedEvent match
      case sequence: Sequence =>
        val updatedBeatPosition =
          incrementBeatPosition(playBackLocation.locatedBeatPosition, sequence.head)
        val swingOffset =
          if updatedBeatPosition.isSwungBeat(song.swing) then swingInMillis else 0.0
        val nextNextNoteTime =
          calculateNextNoteTime(playBackLocation.adjustedNoteTime, sequence.head, swingOffset)
        IO.whenA(context.trackIndex.value == 0)(beatPositionRef.set(
          BeatPosition(updatedBeatPosition.value + context.beatPositionOffset))) >>
          track.playAtomicMusicalEvent(
            sequence.head,
            playBackLocation.adjustedNoteTime.value,
            song.tempo) >>
          scheduleSequence(context.copy(
            remainingEvent = sequence.tail,
            nextNoteTime = nextNextNoteTime,
            beatPosition = updatedBeatPosition,
            startingBeatPosition = None,
            swingCompensation = SwingOffset.compensation(swingOffset)
          ))
      case atomicEvent: AtomicMusicalEvent =>
        val updatedBeatPosition =
          incrementBeatPosition(playBackLocation.locatedBeatPosition, atomicEvent)
        val nextNextNoteTime =
          calculateNextNoteTime(playBackLocation.adjustedNoteTime, atomicEvent, swingOffset = 0.0)
        IO.whenA(context.trackIndex.value == 0)(beatPositionRef.set(
          BeatPosition(updatedBeatPosition.value + context.beatPositionOffset))) >>
          track.playAtomicMusicalEvent(atomicEvent, context.nextNoteTime.value, song.tempo)
            .as(nextNextNoteTime)
    end match
  end playNextNote

  private def applyLiveUpdate(
      context: ScheduleContext,
      updatedEvent: MusicalEvent,
      song: Song
  )(using AudioContext): IO[NextNoteTime] =
    // updatedEvent is the whole new musicalEvent from the start so we need to locate where to start from
    val (eventFromCurrentPosition, currentBeatPosition) =
      findEventAtBeatPosition(updatedEvent, context.beatPosition)

    val adjustedNoteTime = adjustNoteTimeForBeatOffset(
      context.nextNoteTime,
      context.beatPosition,
      currentBeatPosition,
      song.tempo)

    // call schedule sequence again with event from current position & timing
    scheduleSequence(context.copy(
      initialEvent = updatedEvent,
      remainingEvent = eventFromCurrentPosition,
      nextNoteTime = adjustedNoteTime,
      beatPosition = currentBeatPosition
    ))

  private def locatePlaybackPosition(
      context: ScheduleContext,
      tempo: Tempo): PlaybackLocation =
    context.startingBeatPosition.fold(
      PlaybackLocation(context.remainingEvent, context.beatPosition, context.nextNoteTime)
    ) { target =>
      val (event, beatPos) = findEventAtBeatPosition(context.remainingEvent, target)
      val adjustedNoteTime = adjustNoteTimeForBeatOffset(
        context.nextNoteTime,
        target,
        beatPos,
        tempo)
      PlaybackLocation(event, beatPos, adjustedNoteTime)
    }

  /*
  Compensating - Event Boundary

  A mismatch happens when two tracks have different subdivision patterns
  I found this with the hi-hats being 8ths and kick being quarter notes

  Say you resume or create a live update at beat 3.5

  Kick (quarter notes, boundaries at 0, 1, 2, 3, 4):
  findEventAtBeatPosition walks: 0 → 1 → 2 → 3 (3 < 3.5, keep going) → 4 (4 >= 3.5, stop).
  Lands at beat 4.0.

  Hihats (eighth notes, boundaries at 0, 0.5, 1.0, ... 3.0, 3.5, 4.0):
  Walks: 0 → 0.5 → ... → 3.0 → 3.5 (3.5 >= 3.5, stop). Lands at beat 3.5.

  Without the adjustment, both would play their first note at the same AudioContext.currentTime
  but the kick is at beat 4.0 and the hihat is at beat 3.5.
  That's half a beat apart, so they'd be out of sync.

  adjustNoteTimeForBeatOffset fixes it: the kick landed 0.5 beats past the target,
  so its first note gets pushed forward by 0.5 beats worth of seconds.
  Now the kick plays at the correct time for beat 4.0 and the hihat
  at the correct time for beat 3.5.

  This method compensates for that gap.
  It takes the difference between where you landed (actualBeatPosition)
  and where you wanted to be (expectedBeatPosition), converts that from
  beats to seconds using the tempo, and shifts noteTime forward by that amount.
  So the note plays at the correct AudioContext time for the beat it's actually at.
   */

  private def adjustNoteTimeForBeatOffset(
      noteTime: NextNoteTime,
      expectedBeatPosition: BeatPosition,
      actualBeatPosition: BeatPosition,
      tempo: Tempo): NextNoteTime =
    val beatOffset = actualBeatPosition.value - expectedBeatPosition.value
    val secondsPerBeat = 60.0 / tempo.value
    NextNoteTime(noteTime.value + beatOffset * secondsPerBeat)
  end adjustNoteTimeForBeatOffset

  /*
  Iterates through sequence from start - Returns the remaining sequence
  from that point and the beat position where it landed.

  Used for two things:
  1. Looking for position in sequence on resume
  2. finding where to continue in a new pattern after a live update.
   */
  private def findEventAtBeatPosition(
      event: MusicalEvent,
      targetBeatPosition: BeatPosition
  ): (MusicalEvent, BeatPosition) =
    @tailrec
    def loop(
        current: MusicalEvent,
        beatPositionAccumulator: BeatPosition): (MusicalEvent, BeatPosition) =
      current match
        case sequence: Sequence =>
          if beatPositionAccumulator.value >= targetBeatPosition.value
          then (current, beatPositionAccumulator)
          else
            loop(
              sequence.tail,
              BeatPosition(beatPositionAccumulator.value + sequence.head.durationToBeats))
        case atomic: AtomicMusicalEvent =>
          (atomic, beatPositionAccumulator)
    // start from the beginning and find event at targetBeatPosition
    loop(event, BeatPosition(0.0))

  @tailrec
  private def totalDurationInBeats(event: MusicalEvent, acc: Double = 0.0): Double =
    event match
      case sequence: Sequence =>
        totalDurationInBeats(sequence.tail, acc + sequence.head.durationToBeats)
      case atomic: AtomicMusicalEvent => acc + atomic.durationToBeats

  private def calculateSwingOffset(song: Song): Double =
    val swingFromTempo = song.swing.amount.value.toDouble / 1000.0 * (60.0 / song.tempo.value)
    song.swing.resolution match
      case SwingResolution.Eighth => swingFromTempo * 20
      case SwingResolution.Sixteenth => swingFromTempo * 16

  private def incrementBeatPosition(
      currentBeatPosition: BeatPosition,
      sequence: MusicalEvent): BeatPosition =
    val durationInBeats = sequence match
      case Sequence(head, tail) => head.durationToBeats
      case e: AtomicMusicalEvent => e.durationToBeats

    BeatPosition(currentBeatPosition.value + durationInBeats)

  extension (beatPosition: BeatPosition)
    def isSwungBeat(swing: Swing): Boolean =
      val beatSubdivision = beatPosition.value % 1.0
      swing.resolution match
        case SwingResolution.Eighth => beatSubdivision >= 0.5 && beatSubdivision < 1.0
        case SwingResolution.Sixteenth => beatSubdivision >= 0.25 && beatSubdivision < 0.5 ||
          beatSubdivision >= 0.75 && beatSubdivision < 1.0

end NoteScheduler

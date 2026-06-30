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
import org.soundsofscala.models.Track.resolveTrack

import scala.annotation.tailrec
import scala.concurrent.duration.DurationDouble

/**
 * Walks through a track's `MusicalEvent` list note-by-note, scheduling each one against the
 * `AudioContext` timeline. The Song is re-read from the Ref before every note, so tempo and pattern
 * changes take effect immediately.
 *
 * @param songRef
 *   A Ref holding the current Song, re-read for live updates
 * @param lookAheadMs
 *   The time in milliseconds to look ahead to determine if the next note should be scheduled
 * @param scheduleAheadTimeSeconds
 *   The time window in seconds in which to schedule notes ahead of time
 */

class NoteScheduler(
    songRef: Ref[IO, Song],
    beatPositionRef: Ref[IO, BeatPosition],
    lookAheadMs: LookAhead,
    scheduleAheadTimeSeconds: ScheduleWindow,
    clickTrack: Track[?]):

  private case class ScheduleContext(
      trackIndex: TrackIndex,
      initialEvent: MusicalEvent,
      remainingEvent: MusicalEvent,
      nextNoteTime: NextNoteTime,
      beatPosition: BeatPosition,
      startingBeatPosition: Option[BeatPosition],
      previousNoteSwingCompensation: SwingOffset,
      absoluteBeatPositionBase: Double = 0.0
  )

  def scheduleTrackSequence(
      trackIndex: TrackIndex,
      track: Track[?],
      nextNoteTime: NextNoteTime,
      startingBeatPosition: Option[BeatPosition],
      absoluteBeatPositionBase: Double
  )(using AudioContext): IO[NextNoteTime] =
    val context = ScheduleContext(
      trackIndex,
      initialEvent = track.musicalEvent,
      remainingEvent = track.musicalEvent,
      nextNoteTime,
      BeatPosition(0.0),
      startingBeatPosition,
      SwingOffset(0.0),
      absoluteBeatPositionBase
    )
    scheduleSequence(context)

  // ==================================
  // Schedule Sequence
  // ==================================

  private def scheduleSequence(context: ScheduleContext)(using AudioContext): IO[NextNoteTime] =
    ScheduleStatus.isReadyToSchedule(context.nextNoteTime, scheduleAheadTimeSeconds) match

      case ScheduleStatus.Waiting =>
        IO.sleep(lookAheadMs.value.millis) >> scheduleSequence(context)

      case ScheduleStatus.Ready =>
        songRef.get.flatMap: song =>
          // we need to resolve the track from the song ref again here to get the latest version in case of live updates
          val track = context.trackIndex.resolveTrack(song, clickTrack)
          val currentEventFromSongRef = track.musicalEvent

          // this compares the event fetched from the song ref with the original event specified when the song started playing
          if currentEventFromSongRef != context.initialEvent
            // if the event has changed, we need to apply a live update and call scheduleSequence again
          then applyLiveUpdate(context, currentEventFromSongRef, song)
          else playNextNote(context, track, song)

  // ==================================
  // Play Next Note
  // ==================================

  private def playNextNote(context: ScheduleContext, track: Track[?], song: Song)(
      using AudioContext): IO[NextNoteTime] =

    val playBackLocation = locatePositionToStartPlayback(context, song.tempo)

    playBackLocation.locatedEvent match

      case sequence: Sequence =>
        val updatedBeatPosition =
          addNoteDurationToBeatPosition(playBackLocation.locatedBeatPosition, sequence.head)

        val (followingNoteTime, swingCompensation) = calculateNextNoteTime(
          playBackLocation.nextNoteTime,
          sequence.head,
          updatedBeatPosition,
          song,
          context.previousNoteSwingCompensation)

        for
          _ <- updateBeatPositionTimeline(context, updatedBeatPosition)
          _ <- track.playAtomicMusicalEvent(
            sequence.head,
            playBackLocation.nextNoteTime.value,
            song.tempo)
          result <- scheduleSequence(context.copy(
            remainingEvent = sequence.tail,
            nextNoteTime = followingNoteTime,
            beatPosition = updatedBeatPosition,
            startingBeatPosition = None,
            previousNoteSwingCompensation = swingCompensation
          ))
        yield result

      case atomicEvent: AtomicMusicalEvent =>
        val updatedBeatPosition =
          addNoteDurationToBeatPosition(playBackLocation.locatedBeatPosition, atomicEvent)
        val (followingNoteTime, _) = calculateNextNoteTime(
          playBackLocation.nextNoteTime,
          atomicEvent,
          updatedBeatPosition,
          song,
          context.previousNoteSwingCompensation)

        for
          _ <- updateBeatPositionTimeline(context, updatedBeatPosition)
          _ <- track.playAtomicMusicalEvent(atomicEvent, context.nextNoteTime.value, song.tempo)
        yield followingNoteTime

  // ==================================
  // Utility methods - ScheduleSequence
  // ==================================

  private val updateBeatPositionTimeline: (ScheduleContext, BeatPosition) => IO[Unit] =
    (context: ScheduleContext, updatedBeatPosition: BeatPosition) =>
      IO.whenA(isClickTrack(context.trackIndex))(beatPositionRef.set(
        BeatPosition(updatedBeatPosition.value + context.absoluteBeatPositionBase)))

  private val isClickTrack: TrackIndex => Boolean =
    (trackIndex: TrackIndex) => trackIndex.value == 0

  private def applyLiveUpdate(
      context: ScheduleContext,
      updatedEvent: MusicalEvent,
      song: Song
  )(using AudioContext): IO[NextNoteTime] =
    // updatedEvent is the whole new musicalEvent from the start so we need to locate where to start from
    val (eventFromCurrentPosition, currentBeatPosition) =
      findEventAtBeatPosition(updatedEvent, context.beatPosition)

    val adjustedNoteTime = alignNoteTimeToActualBeatPosition(
      context.nextNoteTime,
      context.beatPosition,
      currentBeatPosition,
      song.tempo)

    // call schedule sequence again with the Event from current position & timing
    scheduleSequence(context.copy(
      initialEvent = updatedEvent,
      remainingEvent = eventFromCurrentPosition,
      nextNoteTime = adjustedNoteTime,
      beatPosition = currentBeatPosition
    ))

  private val locatePositionToStartPlayback: (ScheduleContext, Tempo) => PlaybackLocation =
    (context: ScheduleContext, tempo: Tempo) =>
      context.startingBeatPosition.fold(
        PlaybackLocation(context.remainingEvent, context.beatPosition, context.nextNoteTime)
      ) { target =>
        val (event, beatPos) = findEventAtBeatPosition(context.remainingEvent, target)
        val adjustedNoteTime = alignNoteTimeToActualBeatPosition(
          context.nextNoteTime,
          target,
          beatPos,
          tempo)
        PlaybackLocation(event, beatPos, adjustedNoteTime)
      }

  /**
   * alignNoteTimeToActualBeatPosition - The ballad of why this method exists
   *
   * This is all brought on by resume playback.
   *
   * A mismatch happens when two tracks have different subdivision patterns - I found this with the
   * hi-hats being 8ths and kick being quarter notes
   *
   * Say you resume or create a live update at beat 3.5
   *
   * Kick (quarter notes, boundaries at 0, 1, 2, 3, 4): findEventAtBeatPosition walks: 0 → 1 → 2 → 3
   * (3 < 3.5, keep going) → 4 (4 >= 3.5, stop). Lands at beat 4.0.
   *
   * Hihats (eighth notes, boundaries at 0, 0.5, 1.0, ... 3.0, 3.5, 4.0): Walks: 0 → 0.5 → ... → 3.0
   * → 3.5 (3.5 >= 3.5, stop). Lands at beat 3.5.
   *
   * Without the adjustment, both would play their first note at the same AudioContext.currentTime
   * but the kick is at beat 4.0 and the hihat is at beat 3.5. That's half a beat apart, so they'd
   * be out of sync.
   *
   * This method fixes it: the kick landed 0.5 beats past the target, so its noteTime gets delayed
   * by 0.5 beats worth of seconds. Now the kick plays at the correct time for beat 4.0 and the
   * hihat at the correct time for beat 3.5.
   */

  private def alignNoteTimeToActualBeatPosition(
      noteTime: NextNoteTime,
      expectedBeatPosition: BeatPosition,
      actualBeatPosition: BeatPosition,
      tempo: Tempo): NextNoteTime =
    val beatOffset = actualBeatPosition.value - expectedBeatPosition.value
    val secondsPerBeat = 60.0 / tempo.value
    NextNoteTime(noteTime.value + beatOffset * secondsPerBeat)

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

  private val calculateSwingOffset: Song => SwingOffset =
    (song: Song) =>
      val swingFromTempo = song.swing.amount.value.toDouble / 1000.0 * (60.0 / song.tempo.value)
      SwingOffset(song.swing.resolution match
        case SwingResolution.Eighth => swingFromTempo * 20
        case SwingResolution.Sixteenth => swingFromTempo * 16
      )

  private def calculateNextNoteTime(
      currentNoteTime: NextNoteTime,
      event: AtomicMusicalEvent,
      nextBeatPosition: BeatPosition,
      song: Song,
      previousSwingCompensation: SwingOffset
  ): (nextNoteTime: NextNoteTime, swingCompensation: SwingOffset) =
    val swingOffset =
      if nextBeatPosition.isSwungBeat(song.swing) then calculateSwingOffset(song)
      else SwingOffset.none
    val nextNoteTime = NextNoteTime(
      currentNoteTime.value + swingOffset.value + previousSwingCompensation.value + event.durationToSeconds(
        song.tempo))
    (nextNoteTime, SwingOffset.compensation(swingOffset.value))

  private val addNoteDurationToBeatPosition: (BeatPosition, MusicalEvent) => BeatPosition =
    (currentBeatPosition: BeatPosition, sequence: MusicalEvent) =>
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

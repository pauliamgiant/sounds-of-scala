package org.soundsofscala.transport

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

import cats.effect.IO
import cats.effect.Ref
import cats.syntax.all.*
import org.scalajs.dom.AudioContext
import org.soundsofscala.models.*
import org.soundsofscala.models.Track.resolveTrack

/**
 * Manages the lifecycle of a single track's playback. Owns the logic for handling loop/one-shot
 * behavior.
 *
 *   1. Reads the current Song from a `Ref[IO, Song]`, extracting the track by index.
 *   2. Calculates where to resume playback from.
 *   3. Calculates the beat offset needed for notes of different lengths at resume position. Takes
 *      into acccout looping.
 *   4. Hands off to the NoteScheduler to schedule the notes.
 */
class TrackScheduler(
    songRef: Ref[IO, Song],
    beatPositionRef: Ref[IO, BeatPosition],
    lookAheadMs: LookAhead,
    scheduleAheadTimeSeconds: ScheduleWindow,
    clickTrack: Track[?]):

  private val noteScheduler =
    NoteScheduler(songRef, beatPositionRef, lookAheadMs, scheduleAheadTimeSeconds, clickTrack)

  def scheduleTrack(
      trackIndex: TrackIndex,
      startingBeatPosition: Option[BeatPosition] = none
  )(using audioContext: AudioContext): IO[Unit] =

    // the convention of using 'loop' for our recursive function is pretty apt here
    def loop(
        nextNoteTime: NextNoteTime,
        songPositionToResumeAt: Option[BeatPosition],
        beatOffsetForResume: Double
    ): IO[Unit] =
      for
        song <- songRef.get
        track = trackIndex.resolveTrack(song, clickTrack)
        _ <- track.playback match

          case Playback.OneShot =>
            noteScheduler.scheduleTrackSequence(
              trackIndex,
              track,
              nextNoteTime,
              startingBeatPosition = songPositionToResumeAt,
              absoluteBeatPositionBase = 0.0
            ).void

          case Playback.Loop =>
            val (beatPositionInLoop, completedLoopsBeatCount, nextLoopCompletedBeatCount) =
              resolveLoopPlaybackPosition(track, songPositionToResumeAt, beatOffsetForResume)
            noteScheduler.scheduleTrackSequence(
              trackIndex,
              track,
              nextNoteTime,
              startingBeatPosition = beatPositionInLoop,
              absoluteBeatPositionBase = completedLoopsBeatCount
            ).flatMap(finalNoteTime =>
              loop(finalNoteTime, songPositionToResumeAt = none, nextLoopCompletedBeatCount))
      yield ()

    val playbackStartTime = NextNoteTime(audioContext.currentTime)

    /**
     * Starting the loop with the current time and a beat offset of 0.0.
     */
    loop(
      playbackStartTime,
      songPositionToResumeAt = startingBeatPosition,
      beatOffsetForResume = 0.0
    )
  end scheduleTrack

  /**
   * When resuming playback (e.g. after pause), the beat position may land partway through a looped
   * sequence. For example, pausing at beat 5.0 on a 4-beat loop means we need to resume at beat 1.0
   * within the pattern (5.0 % 4.0 = 1.0).
   *
   * The beat offset accounts for how many complete loops have already elapsed. This is important
   * because the `beatPositionRef` (used for UI display and pause/resume) needs to reflect the
   * absolute position in the song, not just the position within the current loop iteration.
   *
   * Without this offset, every loop restart would reset the beat position to 0, losing track of
   * where we actually are in the song timeline.
   *
   * @param track
   *   The track whose musical event defines the loop length
   * @param songPositionToResumeAt
   *   The absolute beat position to resume from (e.g. 5.0), or None if starting fresh
   * @param currentBeatOffset
   *   The accumulated beat offset from previous loop iterations
   * @return
   *   A tuple of:
   *   - The beat position within the current loop iteration to resume from (e.g. 1.0)
   *   - The total beats from completed loop iterations (so beatPositionRef reflects absolute song
   *     position)
   *   - The beat offset to pass to the next loop iteration (completed loops + one full pattern
   *     length)
   */
  private def resolveLoopPlaybackPosition(
      track: Track[?],
      songPositionToResumeAt: Option[BeatPosition],
      currentBeatOffset: Double
  ): (Option[BeatPosition], Double, Double) =
    val totalBeatsInEvent = track.musicalEvent.totalDurationInBeats

    // what part of the loop to play
    val beatPositionInLoopToResumeAt =
      songPositionToResumeAt.map(position => BeatPosition(position.value % totalBeatsInEvent))

    // how many beats of completed loops have elapsed before this - keep track of absolute song position
    val completedLoopsBeatCount = songPositionToResumeAt.fold(currentBeatOffset)(position =>
      position.value - (position.value % totalBeatsInEvent))

    val nextLoopCompletedBeatCount = completedLoopsBeatCount + totalBeatsInEvent

    (beatPositionInLoopToResumeAt, completedLoopsBeatCount, nextLoopCompletedBeatCount)
  end resolveLoopPlaybackPosition
end TrackScheduler

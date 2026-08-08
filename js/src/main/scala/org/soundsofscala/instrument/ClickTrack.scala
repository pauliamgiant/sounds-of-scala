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

package org.soundsofscala.instrument

import cats.effect.{IO, Ref}
import org.scalajs.dom
import org.scalajs.dom.{AudioContext, AudioNode}
import org.soundsofscala.graph.AudioNode.*
import org.soundsofscala.graph.AudioParam
import org.soundsofscala.graph.AudioParam.AudioParamEvent.*
import org.soundsofscala.models.*
import org.soundsofscala.syntax.all.*

object ClickTrack:
  case class Settings(volume: Double = 0.3, frequency: Double = 1000.0)

  given Default[Settings] with
    val default: Settings = Settings()

  val pattern: MusicalEvent = (C4.medium.sixteenth + r16) * 8

  def track(instrument: ClickTrack): Track[Settings] =
    Track(Title("ClickTrack"), pattern, instrument, Playback.Loop)

  def apply()(using audioContext: AudioContext): IO[ClickTrack] =
    for
      activeNodesRef <- Ref.of[IO, Set[AudioNode]](Set.empty)
      mutedRef <- Ref.of[IO, Boolean](true)
    yield new ClickTrack(activeNodesRef, mutedRef)
end ClickTrack

final class ClickTrack private (
    protected val activeNodesRef: Ref[IO, Set[AudioNode]],
    private val mutedRef: Ref[IO, Boolean]
)(using audioContext: AudioContext)
    extends Instrument[ClickTrack.Settings]:

  def mute: IO[Unit] = mutedRef.set(true)
  def unmute: IO[Unit] = mutedRef.set(false)
  def toggleMute: IO[Boolean] = mutedRef.modify(muteStatus => (!muteStatus, !muteStatus))
  def isMuted: IO[Boolean] = mutedRef.get

  private val clickDuration = 0.05

  override protected def playWithSettings(
      musicEvent: AtomicMusicalEvent,
      when: Double,
      tempo: Tempo,
      settings: ClickTrack.Settings
  )(using dom.AudioContext): IO[Unit] =
    mutedRef.get.flatMap:
      case true => IO.unit
      case false =>
        IO {
          val osc = sineOscillator(when, clickDuration)
            .withFrequency(AudioParam(Vector(SetValueAtTime(settings.frequency, when))))

          val gain = Gain(
            List.empty,
            AudioParam(Vector(
              SetValueAtTime(settings.volume, when),
              ExponentialRampToValueAtTime(0.001, when + clickDuration)
            )))

          val graph = osc --> gain
          graph.create.connect(audioContext.destination)
        }
end ClickTrack

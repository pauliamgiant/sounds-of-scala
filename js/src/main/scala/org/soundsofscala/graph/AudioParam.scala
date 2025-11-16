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

package org.soundsofscala.graph

import org.scalajs.dom
import org.scalajs.dom.AudioContext
import org.soundsofscala.graph.AudioNode.AudioSource
import org.soundsofscala.graph.AudioParam.AudioParamEvent.*

final case class AudioParam(events: Vector[AudioParam.AudioParamEvent]):
  import AudioParam.AudioParamEvent

  /**
   * Set all the values in this AudioParam to the given dom.AudioParam
   */
  def set(target: dom.AudioParam)(using context: AudioContext): Unit =
    events.foreach:
      case SetValueAtTime(value, startTime) =>
        target.setValueAtTime(value, startTime)
      case LinearRampToValueAtTime(value, endTime) =>
        target.linearRampToValueAtTime(value, endTime)
      case ExponentialRampToValueAtTime(value, endTime) =>
        target.exponentialRampToValueAtTime(value, endTime)
      case ConnectToAudioNode(audioSource) =>
        connectToAudioNode(audioSource)(target)

  def setValueAtTime(value: Double, startTime: Double): AudioParam =
    this.copy(events = events :+ AudioParamEvent.SetValueAtTime(value, startTime))

  def exponentialRampToValueAtTime(value: Double, endTime: Double): AudioParam =
    this.copy(events = events :+ AudioParamEvent.ExponentialRampToValueAtTime(value, endTime))

  def linearRampToValueAtTime(value: Double, endTime: Double): AudioParam =
    this.copy(events = events :+ AudioParamEvent.LinearRampToValueAtTime(value, endTime))

  private def connectToAudioNode(audioSource: AudioSource)(using
                                                           context: AudioContext): dom.AudioParam => Unit =
    audioSource.create.connect
end AudioParam

object AudioParam:
  val empty: AudioParam = AudioParam(Vector.empty)

  enum AudioParamEvent:
    case SetValueAtTime(value: Double, startTime: Double)
    case LinearRampToValueAtTime(value: Double, endTime: Double)
    case ExponentialRampToValueAtTime(value: Double, endTime: Double)
    case ConnectToAudioNode(audioSource: AudioSource)

  extension (audioParam: AudioParam)
    def +(other: AudioParam): AudioParam =
      AudioParam(audioParam.events ++ other.events)

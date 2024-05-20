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

package org.soundsofscala.synthesis

import org.scalajs.dom.{AudioContext, AudioNode, GainNode}
import org.soundsofscala.models.Volume

/**
 * An amplifier that can be used to control the volume of a signal.
 * @param audioContext
 *   The audio context to use for creating the gain node.
 */
case class Amplifier()(using audioContext: AudioContext):
  private val gainNode: GainNode = audioContext.createGain()
  def plugInTo(node: AudioNode): Unit = gainNode.connect(node)
  def plugIn(node: AudioNode): Unit = node.connect(gainNode)
  def setLevelIndiscriminately(vol: Volume): Unit = gainNode.gain.value = vol.value
  def level(vol: Volume, when: Double): Unit =
    gainNode.gain.value = 0
    gainNode.gain.linearRampToValueAtTime(vol.value, when + 0.06)

  def quickFade(stopTime: Double): Unit =
    gainNode.gain.linearRampToValueAtTime(0.0001, Math.max(stopTime, stopTime + 0.05))

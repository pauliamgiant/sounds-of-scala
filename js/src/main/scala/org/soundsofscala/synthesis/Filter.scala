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

import org.scalajs.dom.AudioContext
import org.scalajs.dom.AudioNode
import org.scalajs.dom.BiquadFilterNode
import org.soundsofscala.models.Bandwidth
import org.soundsofscala.models.Frequency

/**
 * Filter for shaping audio signal with different types of filters
 * @param frequency
 *   frequency of the filter
 * @param bandwidth
 *   bandwidth of the filter - how wide the filter is
 * @param filterType
 *   type of the filter - lowpass, highpass, bandpass, notch, allpass, peaking, lowshelf, highshelf
 * @param audioContext
 *   audio context for creating filter node
 */
enum Filter(frequency: Frequency, bandwidth: Bandwidth, filterType: String)(
    using audioContext: AudioContext):
  private val filterNode: BiquadFilterNode = audioContext.createBiquadFilter()
  filterNode.`type` = filterType
  filterNode.frequency.value = frequency.value
  filterNode.Q.value = bandwidth.value

  def updateF(f: Double): Unit = filterNode.frequency.value = f

  def updateQ(q: Double): Unit = filterNode.Q.value = q

  def plugInTo(node: AudioNode): Unit = filterNode.connect(node)

  def plugIn(node: AudioNode): BiquadFilterNode =
    node.connect(filterNode)
    filterNode
  case LowPass(frequency: Frequency, bandwidth: Bandwidth)(using audioContext: AudioContext)
      extends Filter(frequency, bandwidth, "lowpass")
  case HighPass(frequency: Frequency, bandwidth: Bandwidth)(using audioContext: AudioContext)
      extends Filter(frequency, bandwidth, "highpass")
  case BandPass(frequency: Frequency, bandwidth: Bandwidth)(using audioContext: AudioContext)
      extends Filter(frequency, bandwidth, "bandpass")
  case Notch(frequency: Frequency, bandwidth: Bandwidth)(using audioContext: AudioContext)
      extends Filter(frequency, bandwidth, "notch")
  case AllPass(frequency: Frequency, bandwidth: Bandwidth)(using audioContext: AudioContext)
      extends Filter(frequency, bandwidth, "allpass")
  case Peaking(frequency: Frequency, bandwidth: Bandwidth)(using audioContext: AudioContext)
      extends Filter(frequency, bandwidth, "peaking")
  case LowShelf(frequency: Frequency, bandwidth: Bandwidth)(using audioContext: AudioContext)
      extends Filter(frequency, bandwidth, "lowshelf")
  case HighShelf(frequency: Frequency, bandwidth: Bandwidth)(using audioContext: AudioContext)
      extends Filter(frequency, bandwidth, "highshelf")
end Filter

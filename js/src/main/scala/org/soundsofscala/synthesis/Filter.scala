package org.soundsofscala.synthesis

import org.scalajs.dom.{AudioContext, AudioNode, BiquadFilterNode}
import org.soundsofscala.models.{Bandwidth, Frequency}

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

package org.soundsofscala.synthesis

import org.scalajs.dom.{AudioContext, AudioNode, BiquadFilterNode}
import refined4s.*

type Frequency = Frequency.Type
object Frequency extends Newtype[Double]

type Bandwidth = Bandwidth.Type
object Bandwidth extends Newtype[Double]

case class Filter(frequency: Frequency, bandwidth: Bandwidth)(using audioContext: AudioContext):
  private val filterNode: BiquadFilterNode = audioContext.createBiquadFilter()
  filterNode.`type` = "lowpass"
  filterNode.frequency.value = frequency.value
  filterNode.Q.value = bandwidth.value
  def updateF(f: Double): Unit = filterNode.frequency.value = f
  def updateQ(q: Double): Unit = filterNode.Q.value = q
  def plugInTo(node: AudioNode): Unit = filterNode.connect(node)
  def plugIn(node: AudioNode): BiquadFilterNode =
    node.connect(filterNode)
    filterNode

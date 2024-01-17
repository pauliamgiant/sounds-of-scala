package com.soundsofscala.synthesis

import org.scalajs.dom.{AudioContext, AudioNode, BiquadFilterNode}
import refined4s.*

type Frequency = Frequency.Type
object Frequency extends Newtype[Double]

type Bandwidth = Bandwidth.Type
object Bandwidth extends Newtype[Double]

case class Filter(frequency: Frequency, bandwidth: Bandwidth)(using audioContext: AudioContext):
//case class Filter()(using audioContext: AudioContext):
  private val filterNode: BiquadFilterNode = audioContext.createBiquadFilter()
  filterNode.`type` = "lowpass"
  filterNode.frequency.value = frequency.value
//  filterNode.frequency.value = 1000
  filterNode.Q.value = bandwidth.value
//  filterNode.Q.value = 1
  def updateF(f: Double): Unit = filterNode.frequency.value = f
  def updateQ(q: Double): Unit = filterNode.Q.value = q
  def plugInTo(node: AudioNode): Unit = filterNode.connect(node)
  def plugIn(node: AudioNode): BiquadFilterNode =
    node.connect(filterNode)
    filterNode

package com.soundsofscala.synthesis

import org.scalajs.dom.{AudioContext, AudioNode, BiquadFilterNode}
case class Filter()(using audioContext: AudioContext):
  private val filterNode: BiquadFilterNode = audioContext.createBiquadFilter()
  filterNode.`type` = "lowpass"
  filterNode.frequency.value = 10000
  filterNode.Q.value = 2
  def updateF(f: Double): Unit = filterNode.frequency.value = f
  def plugInTo(node: AudioNode): Unit = filterNode.connect(node)
  def plugIn(node: AudioNode): BiquadFilterNode =
    node.connect(filterNode)
    filterNode

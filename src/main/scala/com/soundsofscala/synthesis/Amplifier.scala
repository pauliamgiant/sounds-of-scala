package com.soundsofscala.synthesis

import org.scalajs.dom.{AudioContext, AudioNode, GainNode}

case class Amplifier()(using audioContext: AudioContext):
  private val gainNode: GainNode = audioContext.createGain()
  def plugInTo(node: AudioNode): Unit = gainNode.connect(node)
  def plugIn(node: AudioNode): Unit = node.connect(gainNode)
  def level(vol: Double): Unit = gainNode.gain.value = vol

  def quickFade(stopTime: Double): Unit =
    gainNode.gain.setTargetAtTime(stopTime, Math.max(stopTime - 0.55, stopTime), .2)

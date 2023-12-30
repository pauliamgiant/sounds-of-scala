package com.soundsofscala.synthesis

import org.scalajs.dom.{AudioContext, AudioNode, GainNode}

case class Amplifier()(using audioContext: AudioContext):
  private val gainNode: GainNode = audioContext.createGain()
  def plugInTo(node: AudioNode): Unit = gainNode.connect(node)
  def plugIn(node: AudioNode): Unit = node.connect(gainNode)
  def level(vol: Double): Unit = gainNode.gain.value = vol
  def setTargetAtTime(target: Double, startTime: Double, timeConstant: Double): Unit =
    gainNode.gain.setTargetAtTime(target, startTime, timeConstant)

  def quickFade(): Unit =
    gainNode.gain.setTargetAtTime(0, audioContext.currentTime - 0.25, .025)

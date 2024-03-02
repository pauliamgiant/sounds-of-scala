package com.soundsofscala.synthesis

import org.scalajs.dom.{AudioContext, AudioNode, GainNode}

case class Amplifier()(using audioContext: AudioContext):
  private val gainNode: GainNode = audioContext.createGain()
  def plugInTo(node: AudioNode): Unit = gainNode.connect(node)
  def plugIn(node: AudioNode): Unit = node.connect(gainNode)
  def level(vol: Double, when: Double): Unit =
    gainNode.gain.value = 0
    gainNode.gain.linearRampToValueAtTime(vol, when + 0.1)
//    gainNode.gain.exponentialRampToValueAtTime(vol, when + 0.2)

  def quickFade(stopTime: Double): Unit =
    gainNode.gain.linearRampToValueAtTime(0.001, Math.max(stopTime + 0.2, stopTime))

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

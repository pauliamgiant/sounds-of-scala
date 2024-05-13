package org.soundsofscala.graph

import org.scalajs.dom
import org.soundsofscala.graph.AudioNode.Gain
import org.soundsofscala.graph.AudioNode.SineOscillator

sealed trait AudioNode:
  import AudioNode.*

  /**
   * Create the described audio graph with the given `AudioContext`.
   */
  def create(context: dom.AudioContext): dom.AudioNode =
    this match
      case Gain(sources, gain) =>
        val node = context.createGain()
        gain.set(node.gain)
        sources.foreach(s => s.create(context).connect(node))
        node
      case SineOscillator(frequency, detune) =>
        val node = context.createOscillator()
        frequency.set(node.frequency)
        detune.set(node.detune)
        node.start()
        node
object AudioNode:
  // ------------------------------------------------------
  // Constructors
  // ------------------------------------------------------
  val sine: SineOscillator =
    SineOscillator(AudioParam.empty, AudioParam.empty)

  // ------------------------------------------------------
  // Types
  // ------------------------------------------------------
  sealed trait AudioSource extends AudioNode:
    def -->(sink: AudioPipe): AudioPipe =
      sink.addSource(this)

  sealed trait AudioPipe extends AudioSource:
    def addSource(source: AudioSource): AudioPipe

  final case class Gain(sources: List[AudioSource], gain: AudioParam) extends AudioPipe:
    def addSource(source: AudioSource): AudioPipe =
      this.copy(sources = sources :+ source)

    def withGain(gain: AudioParam): Gain =
      this.copy(gain = gain)

  final case class SineOscillator(frequency: AudioParam, detune: AudioParam) extends AudioSource:
    def withFrequency(frequency: AudioParam): SineOscillator =
      this.copy(frequency = frequency)

    def withDetune(detune: AudioParam): SineOscillator =
      this.copy(detune = detune)
end AudioNode

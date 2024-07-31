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

package org.soundsofscala.graph

import org.scalajs.dom
import org.scalajs.dom.AudioContext
import org.soundsofscala.models.AudioTypes.{FilterModel, WaveType}

import scala.annotation.targetName

sealed trait AudioNode:
  import AudioNode.*

  /**
   * Create the described audio graph with the given `AudioContext`.
   */

  final def create(using context: dom.AudioContext): dom.AudioNode =
    this match
      case Gain(sources, gainParam) =>
        val gainNode = context.createGain()
        gainParam.set(gainNode.gain)
        println(s"Creating gain node with gain: ${gainNode.gain.value}")
        sources.foreach(source => source.create.connect(gainNode))
        gainNode.connect(context.destination)
        gainNode

      case Filter(sources, frequency, bandWidth, filterModel) =>
        val filterNode = context.createBiquadFilter()
        frequency.set(filterNode.frequency)
        filterNode.`type` = filterModel match
          case FilterModel.LowPass => "lowpass"
          case FilterModel.HighPass => "highpass"
          case FilterModel.BandPass => "bandpass"
          case FilterModel.LowShelf => "lowshelf"
          case FilterModel.HighShelf => "highshelf"
          case FilterModel.Peaking => "peaking"
          case FilterModel.Notch => "notch"
          case FilterModel.AllPass => "allpass"
        bandWidth.set(filterNode.Q)
        sources.foreach(source => source.create.connect(filterNode))
        filterNode

      case SineOscillator(frequency, detune) =>
        buildOscillatorNode(WaveType.Sine, frequency, detune)

      case SawtoothOscillator(frequency, detune) =>
        buildOscillatorNode(WaveType.Sawtooth, frequency, detune)

      case TriangleOscillator(frequency, detune) =>
        buildOscillatorNode(WaveType.Triangle, frequency, detune)

      case SquareOscillator(frequency, detune) =>
        buildOscillatorNode(WaveType.Square, frequency, detune)

  private def buildOscillatorNode(
      waveType: WaveType,
      frequency: AudioParam,
      detune: AudioParam)(using context: AudioContext): dom.OscillatorNode =
    val oscillatorNode = context.createOscillator()
    oscillatorNode.`type` = waveType.toString
    detune.set(oscillatorNode.detune)
    frequency.set(oscillatorNode.frequency)
    oscillatorNode.start()
    oscillatorNode

end AudioNode
object AudioNode:

  // ------------------------------------------------------
  // Constructors
  // ------------------------------------------------------

  val sine: SineOscillator =
    SineOscillator(AudioParam.empty, AudioParam.empty)

  val sawtooth: SawtoothOscillator =
    SawtoothOscillator(AudioParam.empty, AudioParam.empty)

  val triangle: TriangleOscillator =
    TriangleOscillator(AudioParam.empty, AudioParam.empty)

  val square: SquareOscillator =
    SquareOscillator(AudioParam.empty, AudioParam.empty)

  val bandPassFilter: Filter = Filter(
    List.empty,
    AudioParam(Vector.empty),
    AudioParam(Vector.empty),
    FilterModel.BandPass
  )

  // ------------------------------------------------------
  // Types
  // ------------------------------------------------------

  sealed trait AudioSource extends AudioNode:
    @targetName("pipeTo")
    def -->(sink: AudioPipe): AudioPipe =
      sink.addSource(this)

  sealed trait AudioPipe extends AudioSource:
    def addSource(source: AudioSource): AudioPipe

  final case class Gain(sources: List[AudioSource], gain: AudioParam) extends AudioPipe:
    def addSource(source: AudioSource): AudioPipe =
      this.copy(sources = sources :+ source)

    def withGain(gain: AudioParam): Gain =
      this.copy(gain = gain)

  final case class Filter(
      sources: List[AudioSource],
      frequency: AudioParam,
      bandWidth: AudioParam,
      filterType: FilterModel)
      extends AudioPipe:

    override def addSource(source: AudioSource): AudioPipe =
      this.copy(sources = sources :+ source)

    def withFrequency(frequency: AudioParam): Filter =
      this.copy(frequency = frequency)

    def withBandWidth(bandWidth: AudioParam): Filter =
      this.copy(bandWidth = bandWidth)
  end Filter

  final case class SineOscillator(frequency: AudioParam, detune: AudioParam) extends AudioSource:
    def withFrequency(frequency: AudioParam): SineOscillator =
      this.copy(frequency = frequency)

    def withDetune(detune: AudioParam): SineOscillator =
      this.copy(detune = detune)

  final case class SawtoothOscillator(frequency: AudioParam, detune: AudioParam)
      extends AudioSource:
    def withFrequency(frequency: AudioParam): SawtoothOscillator =
      this.copy(frequency = frequency)

    def withDetune(detune: AudioParam): SawtoothOscillator =
      this.copy(detune = detune)

  final case class TriangleOscillator(frequency: AudioParam, detune: AudioParam)
      extends AudioSource:
    def withFrequency(frequency: AudioParam): TriangleOscillator =
      this.copy(frequency = frequency)

    def withDetune(detune: AudioParam): TriangleOscillator =
      this.copy(detune = detune)

  final case class SquareOscillator(frequency: AudioParam, detune: AudioParam) extends AudioSource:
    def withFrequency(frequency: AudioParam): SquareOscillator =
      this.copy(frequency = frequency)

    def withDetune(detune: AudioParam): SquareOscillator =
      this.copy(detune = detune)

end AudioNode

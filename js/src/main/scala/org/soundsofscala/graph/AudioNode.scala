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
import org.scalajs.dom.{AudioContext, DelayNode}
import org.soundsofscala.models.AudioTypes.FilterModel
import org.soundsofscala.models.AudioTypes.WaveType

import scala.annotation.targetName
import scala.scalajs.js.typedarray.Float32Array

sealed trait AudioNode:
  import AudioNode.*

  /**
   * Create the described audio graph with the given `AudioContext`.
   */

  final def play(using context: dom.AudioContext): dom.AudioContext ?=> Unit =
    val node = create
    println(s"Playing node: $node")
    node.connect(context.destination)

  final def create(using context: dom.AudioContext): dom.AudioNode =
    this match
      case Gain(sources, gainParam) =>
        val gainNode = context.createGain()
        gainParam.set(gainNode.gain)
        println(s"Creating gain node with gain: ${gainNode.gain.value}")
        sources.foreach(source => source.create.connect(gainNode))
        gainNode

      case Panner(sources, panParam) =>
        val pannerNode = context.createStereoPanner()
        panParam.set(pannerNode.pan)
        sources.foreach(source => source.create.connect(pannerNode))
        pannerNode

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

      case Delay(sources, delayTime) =>
        val delayNode: DelayNode = context.createDelay(maxDelayTime = 100)
        delayTime.set(delayNode.delayTime)
        sources.foreach(source => source.create.connect(delayNode))
        delayNode

      case SineOscillator(when, duration, frequency, detune) =>
        buildOscillatorNode(when, duration, WaveType.Sine, frequency, detune)

      case SawtoothOscillator(when, duration, frequency, detune) =>
        buildOscillatorNode(when, duration, WaveType.Sawtooth, frequency, detune)

      case TriangleOscillator(when, duration, frequency, detune) =>
        buildOscillatorNode(when, duration, WaveType.Triangle, frequency, detune)

      case SquareOscillator(when, duration, frequency, detune) =>
        buildOscillatorNode(when, duration, WaveType.Square, frequency, detune)

      case WaveTableOscillator(when, duration, frequency, detune, realArray, imagArray) =>
        val waveTable = context.createPeriodicWave(realArray, imagArray)
        val oscillatorNode: dom.OscillatorNode = context.createOscillator()
        oscillatorNode.setPeriodicWave(waveTable)
        detune.set(oscillatorNode.detune)
        frequency.set(oscillatorNode.frequency)
        oscillatorNode.start(when)
        oscillatorNode.stop(when + duration + 0.4)
        oscillatorNode

  private def buildOscillatorNode(
      when: Double,
      duration: Double,
      waveType: WaveType,
      frequency: AudioParam,
      detune: AudioParam)(using context: AudioContext): dom.OscillatorNode =
    val oscillatorNode = context.createOscillator()
    oscillatorNode.`type` = waveType.toString.toLowerCase
    detune.set(oscillatorNode.detune)
    frequency.set(oscillatorNode.frequency)
    oscillatorNode.start(when)
    oscillatorNode.stop(when + duration)
    oscillatorNode
end AudioNode
object AudioNode:

  // ------------------------------------------------------
  // Constructors
  // ------------------------------------------------------

  def sineOscillator(when: Double, duration: Double): SineOscillator =
    SineOscillator(when, duration, AudioParam.empty, AudioParam.empty)

  def sawtoothOscillator(when: Double, duration: Double): SawtoothOscillator =
    SawtoothOscillator(when, duration, AudioParam.empty, AudioParam.empty)

  def triangleOscillator(when: Double, duration: Double): TriangleOscillator =
    TriangleOscillator(when, duration, AudioParam.empty, AudioParam.empty)

  def squareOscillator(when: Double, duration: Double): SquareOscillator =
    SquareOscillator(when, duration, AudioParam.empty, AudioParam.empty)

  def waveTableOscillator(
      when: Double,
      duration: Double,
      realArray: Float32Array,
      imaginaryArray: Float32Array): WaveTableOscillator =
    WaveTableOscillator(
      when,
      duration,
      AudioParam.empty,
      AudioParam.empty,
      realArray,
      imaginaryArray)

  val panControl: Panner = Panner(
    List.empty,
    AudioParam(Vector.empty)
  )

  val bandPassFilter: Filter = Filter(
    List.empty,
    AudioParam(Vector.empty),
    AudioParam(Vector.empty),
    FilterModel.BandPass
  )

  val lowPassFilter: Filter = Filter(
    List.empty,
    AudioParam(Vector.empty),
    AudioParam(Vector.empty),
    FilterModel.LowPass
  )

  val delay: Delay = Delay(
    List.empty,
    AudioParam(Vector.empty)
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

  final case class Delay(sources: List[AudioSource], delayTime: AudioParam) extends AudioPipe:
    def addSource(source: AudioSource): AudioPipe =
      this.copy(sources = sources :+ source)

    def withDelayTime(delayTime: AudioParam): Delay =
      this.copy(delayTime = delayTime)

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

  final case class Panner(
      sources: List[AudioSource],
      pan: AudioParam)
      extends AudioPipe:
    def addSource(source: AudioSource): AudioPipe =
      this.copy(sources = sources :+ source)

    def withPan(pan: AudioParam): Panner =
      this.copy(pan = pan)

  final case class SineOscillator(
      when: Double,
      duration: Double,
      frequency: AudioParam,
      detune: AudioParam)
      extends AudioSource:
    def withFrequency(frequency: AudioParam): SineOscillator =
      this.copy(frequency = frequency)

    def withDetune(detune: AudioParam): SineOscillator =
      this.copy(detune = detune)

  final case class SawtoothOscillator(
      when: Double,
      duration: Double,
      frequency: AudioParam,
      detune: AudioParam
  ) extends AudioSource:
    def withFrequency(frequency: AudioParam): SawtoothOscillator =
      this.copy(frequency = frequency)

    def withDetune(detune: AudioParam): SawtoothOscillator =
      this.copy(detune = detune)

  final case class TriangleOscillator(
      when: Double,
      duration: Double,
      frequency: AudioParam,
      detune: AudioParam)
      extends AudioSource:
    def withFrequency(frequency: AudioParam): TriangleOscillator =
      this.copy(frequency = frequency)

    def withDetune(detune: AudioParam): TriangleOscillator =
      this.copy(detune = detune)

  final case class SquareOscillator(
      when: Double,
      duration: Double,
      frequency: AudioParam,
      detune: AudioParam)
      extends AudioSource:
    def withFrequency(frequency: AudioParam): SquareOscillator =
      this.copy(frequency = frequency)

    def withDetune(detune: AudioParam): SquareOscillator =
      this.copy(detune = detune)

  final case class WaveTableOscillator(
      when: Double,
      duration: Double,
      frequency: AudioParam,
      detune: AudioParam,
      realArray: Float32Array,
      imaginaryArray: Float32Array)
      extends AudioSource:

    def withFrequency(frequency: AudioParam): WaveTableOscillator =
      this.copy(frequency = frequency)

    def withDetune(detune: AudioParam): WaveTableOscillator =
      this.copy(detune = detune)
end AudioNode

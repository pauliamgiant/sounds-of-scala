package com.soundsofscala.synthesis

import jdk.jfr.Frequency
import org.scalajs.dom.{AudioContext, GainNode, OscillatorNode}

enum WaveType:
  case Sine, Square, Sawtooth, Triangle

object Oscillator:
  def apply(
      waveType: WaveType,
      frequency: Double = 440,
      volume: Double = 0.7): AudioContext ?=> Oscillator =
    waveType match
      case WaveType.Sine => SineOscillator(frequency, volume)
      case WaveType.Square => SquareOscillator(frequency, volume)
      case WaveType.Sawtooth => SawtoothOscillator(frequency, volume)
      case WaveType.Triangle => TriangleOscillator(frequency, volume)

enum Oscillator(frequency: Double, volume: Double)(using val audioContext: AudioContext):

  def start(): Unit =
    oscillatorNode.`type` = waveTypeToString(this)
    oscillatorNode.frequency.value = frequency
    val gainNode = audioContext.createGain()
    gainNode.connect(audioContext.destination)
    gainNode.gain.value = volume
    oscillatorNode.connect(gainNode)
    oscillatorNode.start()

  def stop(): Unit = oscillatorNode.stop()

  def volume(volume: Double): Oscillator =
    this match
      case SineOscillator(freq, _) => SineOscillator(freq, volume)
      case SquareOscillator(freq, _) => SquareOscillator(freq, volume)
      case SawtoothOscillator(freq, _) => SawtoothOscillator(freq, volume)
      case TriangleOscillator(freq, _) => TriangleOscillator(freq, volume)

  def frequency(newFrequency: Double): Oscillator =
    this match
      case SineOscillator(_, vol) => SineOscillator(newFrequency, vol)
      case SquareOscillator(_, vol) => SquareOscillator(newFrequency, vol)
      case SawtoothOscillator(_, vol) => SawtoothOscillator(newFrequency, vol)
      case TriangleOscillator(_, vol) => TriangleOscillator(newFrequency, vol)

  def updateFrequency(frequency: Double): Unit = oscillatorNode.frequency.value = frequency

  private def waveTypeToString(oscillator: Oscillator): String =
    oscillator match
      case _: SineOscillator => "sine"
      case _: SquareOscillator => "square"
      case _: SawtoothOscillator => "sawtooth"
      case _: TriangleOscillator => "triangle"

  private val oscillatorNode: OscillatorNode = audioContext.createOscillator()
  case SineOscillator(frequency: Double = 440, volume: Double = 0.5)(using AudioContext)
      extends Oscillator(frequency, volume)

  case SquareOscillator(frequency: Double = 440, volume: Double = 0.5)(using AudioContext)
      extends Oscillator(frequency, volume)

  case SawtoothOscillator(frequency: Double = 440, volume: Double = 0.5)(using AudioContext)
      extends Oscillator(frequency, volume)

  case TriangleOscillator(frequency: Double = 440, volume: Double = 0.5)(using AudioContext)
      extends Oscillator(frequency, volume)

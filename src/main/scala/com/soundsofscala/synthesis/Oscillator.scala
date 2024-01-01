package com.soundsofscala.synthesis

import com.soundsofscala.models.Pitch
import org.scalajs.dom.{AudioContext, GainNode, OscillatorNode}

enum WaveType:
  case Sine, Square, Sawtooth, Triangle

object Oscillator:
  def apply(
      waveType: WaveType,
      frequency: Double = 440,
      volume: Double = 0.7
  ): AudioContext ?=> Oscillator =
    waveType match
      case WaveType.Sine => SineOscillator(frequency, volume)
      case WaveType.Square => SquareOscillator(frequency, volume)
      case WaveType.Sawtooth => SawtoothOscillator(frequency, volume)
      case WaveType.Triangle => TriangleOscillator(frequency, volume)

  def stringToWaveType(waveType: String): WaveType =
    waveType match
      case "Sine" => WaveType.Sine
      case "Square" => WaveType.Square
      case "Sawtooth" => WaveType.Sawtooth
      case "Triangle" => WaveType.Triangle

enum Oscillator(frequency: Double, volume: Double)(using audioContext: AudioContext):
  private val oscillatorNode: OscillatorNode = audioContext.createOscillator()
  private val amplifier = Amplifier()
  private val bandpass = Filter()
  oscillatorNode.frequency.value = frequency
  oscillatorNode.`type` = this match
    case _: SineOscillator => "sine"
    case _: SquareOscillator => "square"
    case _: SawtoothOscillator => "sawtooth"
    case _: TriangleOscillator => "triangle"
  oscillatorNode.start()

  def updateFilterFrequency(frequency: Double): Unit = bandpass.updateF(frequency)
  def updateFrequency(frequency: Double): Unit = oscillatorNode.frequency.value = frequency
  def updateFrequencyFromPitch(pitch: Pitch): Unit =
    oscillatorNode.frequency.value = pitch match
      case Pitch.C => pitch.calculateFrequency
      case Pitch.D => pitch.calculateFrequency
      case Pitch.E => pitch.calculateFrequency
      case Pitch.F => pitch.calculateFrequency
      case Pitch.G => pitch.calculateFrequency
      case Pitch.A => pitch.calculateFrequency
      case Pitch.B => pitch.calculateFrequency

  def updateVolume(volume: Double): Unit = amplifier.level(volume)

  def play(vol: Double = volume): Unit =
    amplifier.level(vol)
    amplifier.plugIn(bandpass.plugIn(oscillatorNode))
    amplifier.plugInTo(audioContext.destination)

  def stop(): Unit = amplifier.quickFade()

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

  private def waveTypeToString(oscillator: Oscillator): String =
    oscillator match
      case _: SineOscillator => "sine"
      case _: SquareOscillator => "square"
      case _: SawtoothOscillator => "sawtooth"
      case _: TriangleOscillator => "triangle"

  case SineOscillator(frequency: Double = 440, volume: Double = 0.3)(using AudioContext)
      extends Oscillator(frequency, volume)

  case SquareOscillator(frequency: Double = 440, volume: Double = 0.3)(using AudioContext)
      extends Oscillator(frequency, volume)

  case SawtoothOscillator(frequency: Double = 440, volume: Double = 0.3)(using AudioContext)
      extends Oscillator(frequency, volume)

  case TriangleOscillator(frequency: Double = 440, volume: Double = 0.3)(using AudioContext)
      extends Oscillator(frequency, volume)

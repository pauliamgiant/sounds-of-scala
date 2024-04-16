package org.soundsofscala.synthesis

import cats.effect.IO
import org.soundsofscala.models
import org.soundsofscala.models.AtomicMusicalEvent.DrumStroke
import org.scalajs.dom
import org.scalajs.dom.AudioContext
import org.soundsofscala.models.{AtomicMusicalEvent, Release, Tempo}
import org.soundsofscala.models.AtomicMusicalEvent.DrumStroke

/**
 * This is a POC of creating drum sounds using the Web Audio API. The sounds are based on the
 * 808 drum machine. TODO: Refactor this to use a Synth API
 */
object DrumGeneration:

  def generateKick808(
      drumStroke: DrumStroke,
      when: Double
  )(using audioContext: AudioContext): IO[Unit] =
    IO:
      val velocity = drumStroke.velocity.getNormalisedVelocity
      val osc = audioContext.createOscillator()
      osc.`type` = "sine"
      osc.frequency.setValueAtTime(80, when)
      osc.frequency.exponentialRampToValueAtTime(30, when + 0.2)
      val gain = audioContext.createGain()

      val filterShelf = audioContext.createBiquadFilter()
      filterShelf.`type` = "lowshelf"
      filterShelf.frequency.value = 300
      filterShelf.gain.value = 5

      gain.gain.linearRampToValueAtTime(velocity * 0.5, when + 0.01)

      gain.gain.exponentialRampToValueAtTime(0.001, when + 0.9)

      osc.connect(filterShelf)
      filterShelf.connect(gain)
      gain.connect(audioContext.destination)
      osc.start(when)
      osc.stop(when + 0.9)

  def generateClap808(
      drumStroke: DrumStroke,
      when: Double
  )(using audioContext: AudioContext): IO[Unit] =
    IO:
      val velocity = drumStroke.velocity.getNormalisedVelocity

      def createNoiseBuffer(audioContext: dom.AudioContext): dom.AudioBuffer =
        val bufferSize = audioContext.sampleRate.toFloat // 1 second of audio
        val buffer =
          audioContext.createBuffer(1, bufferSize.toInt, audioContext.sampleRate.toInt)
        val output = buffer.getChannelData(0)
        (0 until output.length).foreach(i => output(i) = (math.random() * 2 - 1).toFloat)
        buffer

      val noiseBuffer = createNoiseBuffer(audioContext)
      val noiseSource = audioContext.createBufferSource()
      noiseSource.buffer = noiseBuffer

      val bandpass = audioContext.createBiquadFilter()
      bandpass.`type` = "bandpass"
      bandpass.frequency.value = 1000
      bandpass.Q.value = 0.2

      val highpass = audioContext.createBiquadFilter()
      highpass.`type` = "highpass"
      highpass.frequency.value = 2000

      val gain = audioContext.createGain()
      gain.gain.setValueAtTime(0, when)
      gain.gain.linearRampToValueAtTime(velocity, when + 0.01)
      gain.gain.exponentialRampToValueAtTime(0.01, when + 0.2)

      noiseSource.connect(bandpass)
      bandpass.connect(highpass)
      highpass.connect(gain)
      gain.connect(audioContext.destination)

      noiseSource.start(when)
      noiseSource.stop(when + 0.2)

  def generateHats808(
      drumStroke: DrumStroke,
      when: Double
  )(using audioContext: AudioContext): IO[Unit] =
    IO:
      val velocity = drumStroke.velocity.getNormalisedVelocity
      val bufferSize = audioContext.sampleRate * 2.0
      val noiseBuffer =
        audioContext.createBuffer(1, bufferSize.toInt, audioContext.sampleRate.toInt)
      val output = noiseBuffer.getChannelData(0)
      (0 until output.length).foreach(i => output(i) = (math.random() * 2 - 1).toFloat)

      val noise = audioContext.createBufferSource()
      noise.buffer = noiseBuffer

      val highPass = audioContext.createBiquadFilter()
      highPass.`type` = "highpass"
      highPass
        .frequency
        .setValueAtTime(10000, when) // High-pass filter frequency, adjust as needed

      val gain = audioContext.createGain()
      gain.gain.setValueAtTime(velocity, when) // Start at full volume
      gain.gain.exponentialRampToValueAtTime(0.01, when + 0.05)

      noise.connect(highPass)
      highPass.connect(gain)
      gain.connect(audioContext.destination)

      noise.start(when)
      noise.stop(when + 0.05)

  def generateSnare808(drumStroke: DrumStroke, when: Double)(
      using audioContext: AudioContext): IO[Unit] =
    IO:
      val velocity = drumStroke.velocity.getNormalisedVelocity

      val bodyOsc = audioContext.createOscillator()
      bodyOsc.`type` = "triangle"
      bodyOsc.frequency.setValueAtTime(170, when)

      val bodyGain = audioContext.createGain()
      bodyGain.gain.setValueAtTime(velocity, when)
      bodyGain.gain.linearRampToValueAtTime(0.01, when + 0.1)

      bodyOsc.connect(bodyGain)

      val bufferSize = audioContext.sampleRate.toInt * 2 // 2 seconds of audio
      val noiseBuffer =
        audioContext.createBuffer(1, bufferSize, audioContext.sampleRate.toInt)
      val output = noiseBuffer.getChannelData(0)

      (0 until output.length).foreach(i => output(i) = (math.random() * 2 - 1).toFloat)
      val noise = audioContext.createBufferSource()
      noise.buffer = noiseBuffer

      val noiseFilter = audioContext.createBiquadFilter()
      noiseFilter.`type` = "bandpass"
      noiseFilter.frequency.setValueAtTime(1000, when)

      noise.connect(noiseFilter)

      val noiseGain = audioContext.createGain()
      noiseGain.gain.setValueAtTime(velocity * .8, when)
      noiseGain.gain.linearRampToValueAtTime(0.01, when + 0.3) // Decay

      noiseFilter.connect(noiseGain)

      bodyGain.connect(audioContext.destination)
      noiseGain.connect(audioContext.destination)

      bodyOsc.start(when)
      noise.start(when)

      bodyOsc.stop(when + 0.3)
      noise.stop(when + 0.3)

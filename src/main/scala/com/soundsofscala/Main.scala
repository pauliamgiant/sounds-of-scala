package com.soundsofscala

import com.soundsofscala.models.*
import com.soundsofscala.models.Accidental.*
import com.soundsofscala.models.Duration.*
import com.soundsofscala.models.MusicalEvent.*
import com.soundsofscala.models.Pitch.*
import com.soundsofscala.models.Velocity.*
import io.github.iltotore.iron.{:|, IronType, autoRefine}
import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.document
import org.scalajs.dom.{
  AudioBuffer,
  AudioBufferSourceNode,
  AudioContext,
  OscillatorNode,
  GainNode
}
import scala.scalajs.js.typedarray.ArrayBuffer

//def buildOscillator(audioContext: AudioContext): OscillatorNode =
//  val oscillator =
//  oscillator

def buildOscillator(
    audioContext: AudioContext,
    gainNode: GainNode,
    waveType: String,
    frequency: Double): OscillatorNode =
  val oscillator = audioContext.createOscillator()
  oscillator.`type` = waveType
  oscillator.frequency.value = frequency
  gainNode.gain.value = -0.10 // Set volume to 25%
  oscillator
  oscillator.connect(gainNode)

  oscillator

@main def main(): Unit =

  val pianoCSample = "/audio/piano/C3.wav"
  val audioContext = new AudioContext()
  val gainNode = audioContext.createGain()
  gainNode.gain.value = 0.1
  gainNode.connect(audioContext.destination)

  val startingFrequency = 220
  val oscillator: OscillatorNode =
    buildOscillator(audioContext, gainNode, "sine", startingFrequency)
  val oscillatorSaw: OscillatorNode =
    buildOscillator(audioContext, gainNode, "sawtooth", startingFrequency * 2)
  val oscillatorSawLow: OscillatorNode =
    buildOscillator(audioContext, gainNode, "sawtooth", startingFrequency / 4)
  val oscillatorSquare: OscillatorNode =
    buildOscillator(audioContext, gainNode, "square", startingFrequency - 3)
  val oscillatorTriangle: OscillatorNode =
    buildOscillator(audioContext, gainNode, "triangle", startingFrequency / 2)

  document.addEventListener(
    "DOMContentLoaded",
    (e: dom.Event) =>
      val homeDiv = document.createElement("div")
      homeDiv.classList.add("homediv")
      document.body.appendChild(homeDiv)

      val heading = document.createElement("h1")
      heading.textContent = "Welcome to Sounds of Scala"
      homeDiv.appendChild(heading)

      val songTitle = document.createElement("h2")
      songTitle.textContent = s"First three notes of the DSL:"
      homeDiv.appendChild(songTitle)

      val firstSong = document.createElement("h2")
      firstSong.textContent = threeNoteMelody()
      homeDiv.appendChild(firstSong)

      val pianoLabel = document.createElement("label")
      pianoLabel.textContent = "Piano Sample"
      dom.document.body.appendChild(pianoLabel)

      val button = document.createElement("button")
      button.textContent = "🎹"
      button.addEventListener(
        "click",
        (e: dom.MouseEvent) => playASingleNote(pianoCSample, audioContext))

      dom.document.body.appendChild(button)

      val simpleLabel = document.createElement("label")
      simpleLabel.textContent = "Simple Sine Synth"
      dom.document.body.appendChild(simpleLabel)

      val simpleSynth = document.createElement("button")
      simpleSynth.textContent = "∿"
      simpleSynth.addEventListener(
        "click",
        (e: dom.MouseEvent) => {
          gainNode.gain.value = -0.400 // not working
          oscillator.start()
        }
      )
      // Append the button to the document
      dom.document.body.appendChild(simpleSynth)

      val compoundSynthLabel = document.createElement("label")
      compoundSynthLabel.textContent = "Compound Synth"
      dom.document.body.appendChild(compoundSynthLabel)

      val synth = document.createElement("button")
      synth.textContent = "🔉"
      synth.addEventListener(
        "click",
        (e: dom.MouseEvent) => {
          gainNode.gain.value = -0.400 // not working
          oscillator.start()
          oscillatorSaw.start()
          oscillatorSawLow.start()
          oscillatorSquare.start()
          oscillatorTriangle.start()

        }
      )
      // Append the button to the document
      dom.document.body.appendChild(synth)

      val slider = dom.document.createElement("input").asInstanceOf[html.Input]
      slider.className = "range"
      slider.id = "frequency"
      slider.`type` = "range"
      slider.min = "20"
      slider.max = "2000"
      slider.step = "0.01"
      slider.value = "220"

      slider.addEventListener(
        "input",
        (_: dom.Event) => {
          val value = slider.value.toDouble
          updateFrequency(value)
        })
      dom.document.body.appendChild(slider)

      def updateFrequency(value: Double): Unit =
        val frequency =
          dom.document.getElementById("frequency").asInstanceOf[html.Input].value.toDouble
        oscillator.frequency.value = frequency
        oscillatorSaw.frequency.value = frequency * 2
        oscillatorSawLow.frequency.value = frequency / 4
        oscillatorSquare.frequency.value = frequency - 3
        oscillatorTriangle.frequency.value = frequency / 2

      val stopSynth = document.createElement("button")
      stopSynth.textContent = "🛑"
      stopSynth.addEventListener(
        "click",
        (e: dom.MouseEvent) =>
          oscillator.stop()
          oscillatorSaw.stop()
          oscillatorSquare.stop()
          oscillatorTriangle.stop()
          oscillatorSawLow.stop()
      )
      // Append the button to the document
      dom.document.body.appendChild(stopSynth)
  )

def playASingleNote(pianoCSample: String, audioContext: AudioContext): Unit =
  loadAudioSample(
    pianoCSample,
    audioContext,
    buffer => {
      val sourceNode = audioContext.createBufferSource()
      sourceNode.buffer = buffer
      sourceNode.connect(audioContext.destination)
      sourceNode.onended = (_: dom.Event) => println("Playback ended.")
      sourceNode.start()
    }
  )
  appendH2(document.body, "The sweet sounds of Scala 🎶")

def appendH2(targetNode: dom.Node, text: String): Unit = {
  val parNode = document.createElement("h2")
  parNode.textContent = text
  targetNode.appendChild(parNode)
}

def threeNoteMelody(): String =
  val firstNote: MusicalEvent = MusicalEvent.Note(
    C,
    Flat,
    Quarter,
    Octave(3),
    OnFull
  )
  val secondNote: MusicalEvent = MusicalEvent.Note(
    D,
    Flat,
    Quarter,
    Octave(3),
    OnFull
  )
  val thirdNote: MusicalEvent = MusicalEvent.Note(
    E,
    Flat,
    Quarter,
    Octave(3),
    OnFull
  )
  (firstNote + secondNote + thirdNote).printEvent()

def loadAudioSample(
    localPath: String,
    audioContext: AudioContext,
    callback: AudioBuffer => Unit): Unit =
  val request = new dom.XMLHttpRequest()
  request.open("GET", localPath, true)
  request.responseType = "arraybuffer"

  request.onload = (_: dom.Event) =>
    val data = request.response.asInstanceOf[ArrayBuffer]

    audioContext.decodeAudioData(
      data,
      buffer => callback(buffer),
      () => println(s"Things have gone sideways for now")
    )
  request.send()

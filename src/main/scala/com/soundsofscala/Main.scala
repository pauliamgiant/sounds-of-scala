package com.soundsofscala

import com.soundsofscala.models.*
import com.soundsofscala.models.Accidental.*
import com.soundsofscala.models.Duration.*
import com.soundsofscala.models.MusicalEvent.*
import com.soundsofscala.models.Pitch
import com.soundsofscala.models.Velocity.*
import com.soundsofscala.synthesis.{Oscillator, WaveType}
import com.soundsofscala.synthesis.WaveType.*
import com.soundsofscala.synthesis.Oscillator.*
import io.github.iltotore.iron.{:|, IronType, autoRefine}
import org.scalajs.dom
import org.scalajs.dom.*

import scala.scalajs.js.typedarray.ArrayBuffer

@main def main(): Unit =

  given audioContext: AudioContext = new AudioContext()

  val pianoCSample = "resources/audio/piano/C3.wav"

  val startingFrequency = 220

  val oscillatorSineBuilt = Oscillator(Sine, startingFrequency * 2).volume(0.5)

  val oscillatorSine = SineOscillator().frequency(startingFrequency).volume(0.2)
  val oscillatorSaw = SawtoothOscillator().frequency(startingFrequency * 2).volume(0.2)
  val oscillatorSawLow = SawtoothOscillator().frequency(startingFrequency / 4).volume(0.2)
  val oscillatorSquare = SquareOscillator().frequency(startingFrequency - 3).volume(0.2)
  val oscillatorTriangle = TriangleOscillator().frequency(startingFrequency / 2).volume(0.2)

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

      val buttonDiv1 = document.createElement("div")
      buttonDiv1.classList.add("button-pad")
      dom.document.body.appendChild(buttonDiv1)

      val button = document.createElement("button")
      button.textContent = "🎹"
      button.addEventListener("click", (e: dom.MouseEvent) => playASingleNote(pianoCSample))
      buttonDiv1.appendChild(button)

      // make 3 notes

      val simpleLabel = document.createElement("label")
      simpleLabel.textContent = "Simple Sine Synthesizer"
      dom.document.body.appendChild(simpleLabel)

      val buttonPad = document.createElement("div")
      buttonPad.classList.add("button-pad")
      dom.document.body.appendChild(buttonPad)

      val CButton = makeAButton(Pitch.C)
      val DButton = makeAButton(Pitch.D)
      val EButton = makeAButton(Pitch.E)
      val FButton = makeAButton(Pitch.F)
      val GButton = makeAButton(Pitch.G)
      val AButton = makeAButton(Pitch.A)
      val BButton = makeAButton(Pitch.B)


      buttonPad.appendChild(CButton)
      buttonPad.appendChild(DButton)
      buttonPad.appendChild(EButton)
      buttonPad.appendChild(FButton)
      buttonPad.appendChild(GButton)
      buttonPad.appendChild(AButton)
      buttonPad.appendChild(BButton)

      dom.document.body.appendChild(buttonPad)

      val compoundSynthLabel = document.createElement("label")
      compoundSynthLabel.textContent = "Messy Compound Synth"
      dom.document.body.appendChild(compoundSynthLabel)

      val buttonDiv2 = document.createElement("div")
      buttonDiv2.classList.add("button-pad")
      dom.document.body.appendChild(buttonDiv2)

      val synth = document.createElement("button")
      synth.textContent = "🔉"
      synth.addEventListener(
        "click",
        (e: dom.MouseEvent) => {
          oscillatorSine.start()
          oscillatorSaw.start()
          oscillatorSawLow.start()
          oscillatorSquare.start()
          oscillatorTriangle.start()

        }
      )
      // Append the button to the document
      buttonDiv2.appendChild(synth)

      val slider = dom.document.createElement("input").asInstanceOf[html.Input]
      slider.className = "range"
      slider.id = "frequency"
      slider.`type` = "range"
      slider.min = "20"
      slider.max = "2000"
      slider.step = "0.01"
      slider.value = "440"

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
        oscillatorSineBuilt.updateFrequency(frequency)
        oscillatorSine.updateFrequency(frequency)
        oscillatorSaw.updateFrequency(frequency * 2)
        oscillatorSawLow.updateFrequency(frequency / 4)
        oscillatorSquare.updateFrequency(frequency - 5)
        oscillatorTriangle.updateFrequency(frequency / 2)

      val stopSynth = document.createElement("button")
      stopSynth.classList.add("stop")
      stopSynth.textContent = "🛑"
      stopSynth.addEventListener(
        "click",
        (e: dom.MouseEvent) =>
          oscillatorSine.stop()
          oscillatorSaw.stop()
          oscillatorSquare.stop()
          oscillatorTriangle.stop()
          oscillatorSawLow.stop()
      )
      // Append the button to the document
      dom.document.body.appendChild(stopSynth)
  )

def playASingleNote(pianoCSample: String)(using audioContext: AudioContext): Unit =
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

//  val testingDSL = C(5).flat.quarter + D(5).flat.quarter + E(5).flat.quarter

def makeAButton(pitch: Pitch): AudioContext ?=> Element =
    val button = document.createElement("button")
    button.textContent = pitch.toString
    button.addEventListener(
      "click",
      (e: dom.MouseEvent) => {
        val osc = SineOscillator()
          .frequency(pitch match
            case Pitch.C => pitch.calculateFrequency
            case Pitch.D => pitch.calculateFrequency
            case Pitch.E => pitch.calculateFrequency
            case Pitch.F => pitch.calculateFrequency
            case Pitch.G => pitch.calculateFrequency
            case Pitch.A => pitch.calculateFrequency
            case Pitch.B => pitch.calculateFrequency
          )
          .volume(0.2)
        osc.start()
        dom.window.setTimeout(() => osc.stop(), 700)
      }
    )
    button

def threeNoteMelody(): String =

  val firstNote: MusicalEvent = MusicalEvent.Note(
    Pitch.C,
    Flat,
    Quarter,
    Octave(3),
    OnFull
  )
  val secondNote: MusicalEvent = MusicalEvent.Note(
    Pitch.D,
    Flat,
    Quarter,
    Octave(3),
    OnFull
  )
  val thirdNote: MusicalEvent = MusicalEvent.Note(
    Pitch.E,
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

package com.soundsofscala

import cats.effect.{ExitCode, IO, IOApp}
import com.soundsofscala.models.*
import com.soundsofscala.models.Accidental.*
import com.soundsofscala.models.Duration.*
import com.soundsofscala.models.Velocity.*
import com.soundsofscala.models.Voice.*
import com.soundsofscala.synthesis.Oscillator.*
import com.soundsofscala.synthesis.WaveType.*
import com.soundsofscala.synthesis.{Oscillator, WaveType}
import com.soundsofscala.transport.Sequencer
import com.soundsofscala.transport.Sequencer.*
import org.scalajs.dom
import org.scalajs.dom.*
import org.scalajs.dom.html.Select

import scala.scalajs.js.typedarray.ArrayBuffer

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = buildUI().as(ExitCode.Success)

  import cats.effect.unsafe.implicits.global

  given audioContext: AudioContext = new AudioContext()
  import MusicalEvent.*

  val aCrotchet = Note(Pitch.A, Natural, Eighth, Octave(3), OnFull)
  val gMinim = Note(Pitch.G, Natural, Half, Octave(3), OnFull)

  val demoMusicalEvent: MusicalEvent =
    C() + C() + G() + G() + aCrotchet + aCrotchet + aCrotchet + aCrotchet + gMinim + F() + F() + E() + E() + D() + D() + C()

  val demoSong: Song =
    Song(
      title = Title("A test sequencer song"),
      swing = Swing(0),
      voices = AllVoices(PianoChannel(demoMusicalEvent))
    )
  private val sequencer: Sequencer =
    SingleVoiceSequencer(demoMusicalEvent, Tempo(200))

  val startingFrequency = 440
  val startingVolume = 0.3
  val waveTypes = List(
    WaveType.Sine,
    WaveType.Sawtooth,
    WaveType.Square,
    WaveType.Triangle
  )

  val oscillatorSine: Oscillator = SineOscillator(startingFrequency)
  val oscillatorSaw: Oscillator = SawtoothOscillator(startingFrequency / 4)
  val oscillatorSquare: Oscillator = SquareOscillator(startingFrequency / 2)
  val oscillatorTriangle: Oscillator = TriangleOscillator(startingFrequency - 3)

  private def updateSynthVolume(waveType: WaveType): Unit =
    val volume =
      dom
        .document
        .getElementById(s"${waveType.toString.toLowerCase}-volume")
        .asInstanceOf[html.Input]
        .value
        .toDouble
    waveType match
      case WaveType.Sine =>
        oscillatorSine.updateVolume(volume)
      case WaveType.Sawtooth =>
        oscillatorSaw.updateVolume(volume)
      case WaveType.Square =>
        oscillatorSquare.updateVolume(volume)
      case WaveType.Triangle =>
        oscillatorTriangle.updateVolume(volume)

  private def updateSynthFilterFrequency(): Unit =
    val frequency =
      dom.document.getElementById("filter-frequency").asInstanceOf[html.Input].value.toDouble
    oscillatorSaw.updateFilterFrequency(frequency)
    oscillatorSine.updateFilterFrequency(frequency)
    oscillatorSquare.updateFilterFrequency(frequency)
    oscillatorTriangle.updateFilterFrequency(frequency)

  private def updateSynthFrequency(): Unit =
    val frequency =
      dom.document.getElementById("frequency").asInstanceOf[html.Input].value.toDouble
    oscillatorSaw.updateFrequency(frequency / 4)
    oscillatorSine.updateFrequency(frequency)
    oscillatorSquare.updateFrequency(frequency / 2)
    oscillatorTriangle.updateFrequency(frequency - 3)

  private def compoundSynthPlayButton(): AudioContext ?=> Element =
    val div = document.createElement("div")
    div.classList.add("button-pad")

    val compoundSynth = document.createElement("button")
    compoundSynth.classList.add("transport-button")
    compoundSynth.textContent = "▶️"

    compoundSynth.addEventListener(
      "click",
      (e: dom.MouseEvent) =>
        def getCurrentVolume(waveType: WaveType) = dom
          .document
          .getElementById(s"${waveType.toString.toLowerCase}-volume")
          .asInstanceOf[html.Input]
          .value
          .toDouble

        oscillatorSaw.play(getCurrentVolume(Sawtooth))
        oscillatorSine.play(getCurrentVolume(Sine))
        oscillatorSquare.play(getCurrentVolume(Square))
        oscillatorTriangle.play(getCurrentVolume(Triangle))
    )
    div.appendChild(compoundSynth)
    div

  private def buildUI(): IO[Unit] = IO {
    document.addEventListener(
      "DOMContentLoaded",
      (e: dom.Event) =>
        val homeDiv = document.createElement("div")
        homeDiv.classList.add("home-div")

        val heading = document.createElement("h1")
        heading.textContent = "Welcome to Sounds of Scala"

        val playground = document.createElement("p")
        playground.textContent = "Feel free to use this playground page to try things out"

        val songTitle = document.createElement("h2")
        songTitle.textContent = s"Testing printing out notes of the Music DSL:"

        val firstSong = document.createElement("h2")
        firstSong.textContent = demoMusicalEvent.toString

        val sequencerLabel = document.createElement("label")
        sequencerLabel.textContent = "Sequencer POC"

        val simpleSineSynthLabel = document.createElement("h1")
        simpleSineSynthLabel.textContent = "Oscillators"

        // Append elements to Document

        homeDiv.append(
          heading,
          playground,
          songTitle,
          firstSong,
          sequencerLabel,
          sequencerButtonDiv(),
          simpleSineSynthLabel,
          buildDropDownOscillatorSelecter(),
          buttonPad(makeAButton),
          buildCompoundSynthPanel()
        )
        document.body.appendChild(homeDiv)
    )
  }

  private def buildCompoundSynthPanel() =
    val compoundSynthContainer = document.createElement("div")
    compoundSynthContainer.classList.add("compound-container")
    val compoundSynthLabel = document.createElement("h1")
    val levelsLabel = document.createElement("h2")
    levelsLabel.textContent = "Oscillator Levels"
    val filterLabel = document.createElement("h2")
    filterLabel.textContent = "Lowpass Filter"
    compoundSynthLabel.textContent = "Compound Synth"
    val levelsContainer = document.createElement("div")
    levelsContainer.classList.add("levels-container")
    val levelsDiv = document.createElement("div")
    levelsDiv.classList.add("levels")
    val filterContainer = document.createElement("div")
    filterContainer.classList.add("levels-container")
    val filterDiv = document.createElement("div")
    filterDiv.classList.add("levels")
    val transport = document.createElement("div")
    transport.classList.add("transport")
    transport.append(
      compoundSynthPlayButton(),
      stopSynthButton()
    )
    filterDiv.append(
      filterLabel,
      buildFilterFreqSlider(10000)
    )
    levelsDiv.append(
      levelsLabel,
      buildVolumeSlider(startingVolume, Sine),
      buildVolumeSlider(startingVolume, Sawtooth),
      buildVolumeSlider(startingVolume * 2, Triangle),
      buildVolumeSlider(startingVolume, Square)
    )

    filterContainer.appendChild(filterDiv)
    levelsContainer.appendChild(levelsDiv)

    compoundSynthContainer.append(
      compoundSynthLabel,
      transport,
      buildPitchSlider(startingFrequency),
      filterContainer,
      levelsContainer,
      buttonPad(compoundSynthButton)
    )
    compoundSynthContainer

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

  def appendH2(targetNode: dom.Node, text: String): Unit = {
    val parNode = document.createElement("h2")
    parNode.textContent = text
    targetNode.appendChild(parNode)
  }

  private def sequencerButtonDiv(): Element =
    val div = document.createElement("div")
    div.classList.add("button-pad")

    val sequencerButtonDiv = document.createElement("button")
    sequencerButtonDiv.textContent = "▶️"
    sequencerButtonDiv.addEventListener(
      "click",
      (e: dom.MouseEvent) => sequencer.play().unsafeRunAndForget()
    ) // 🙄
    div.appendChild(sequencerButtonDiv)
    div

  private def buildDropDownOscillatorSelecter(): Element =
    val div = document.createElement("div")
    div.classList.add("button-pad")

    val select = document.createElement("select").asInstanceOf[html.Select]
    select.id = "oscillator"
    select.classList.add("oscillator")
    val options = waveTypes.map(waveSelectionOptions)
    select.append(options: _*)
    div.appendChild(select)
    div

  private def waveSelectionOptions(waveType: WaveType): Element =
    val option = document.createElement("option")
    option.textContent = waveType.toString
    option

  def buttonPad(f: Pitch => Element): Element =
    val buttonPad = document.createElement("div")
    buttonPad.classList.add("button-pad")

    val buttonList = List(
      Pitch.C,
      Pitch.D,
      Pitch.E,
      Pitch.F,
      Pitch.G,
      Pitch.A,
      Pitch.B
    ).map(f)

    buttonPad.append(buttonList: _*)
    buttonPad

// val testingDSL = C(5).flat.quarter + D(5).flat.quarter + E(5).flat.quarter

  def makeAButton(pitch: Pitch): AudioContext ?=> Element =
    val button = document.createElement("button")
    button.textContent = pitch.toString
    button.addEventListener(
      "click",
      (_: dom.MouseEvent) =>
        val waveType = Oscillator.stringToWaveType(
          document.getElementById("oscillator").asInstanceOf[Select].value)
        val oscillator = Oscillator(waveType = waveType).volume(0.5)
        oscillator.updateFrequencyFromPitch(pitch)
        oscillator.play()
        dom.window.setTimeout(() => oscillator.stop(), 1000)
    )
    button

  private def buildVolumeSlider(startingV: Double, waveType: WaveType) =
    val div = document.createElement("div")
    val label = document.createElement("label")
    label.textContent = s"${waveType.toString}"
    label.classList.add("vol-label")
    div.appendChild(label)
    div.classList.add("slider-pad")
    val slider = dom.document.createElement("input").asInstanceOf[html.Input]
    slider.className = "range"
    slider.classList.add("volume")
    slider.id = s"${waveType.toString.toLowerCase}-volume"
    slider.`type` = "range"
    slider.min = "0"
    slider.max = "1"
    slider.step = "0.01"
    slider.value = startingV.toString

    slider.addEventListener(
      "input",
      (_: dom.Event) => updateSynthVolume(waveType)
    )
    div.appendChild(slider)
    div

  private def buildFilterFreqSlider(startingF: Int) =
    val sliderContainer = document.createElement("div")
    sliderContainer.classList.add("slider-container")
    val label = document.createElement("label")
    label.textContent = "Frequency"
    val slider = dom.document.createElement("input").asInstanceOf[html.Input]
    slider.className = "range"
    slider.id = "filter-frequency"
    slider.`type` = "range"
    slider.min = "20"
    slider.max = "12000"
    slider.step = "0.1"
    slider.value = startingF.toString

    slider.addEventListener(
      "input",
      (_: dom.Event) => updateSynthFilterFrequency()
    )
    sliderContainer.append(label, slider)
    sliderContainer

  private def buildPitchSlider(startingF: Int) =
    val sliderContainer = document.createElement("div")
    sliderContainer.classList.add("slider-container")
    val label = document.createElement("label")
    label.textContent = "Frequency"
    val slider = dom.document.createElement("input").asInstanceOf[html.Input]
    slider.className = "range"
    slider.id = "frequency"
    slider.`type` = "range"
    slider.min = "20"
    slider.max = "1000"
    slider.step = "0.01"
    slider.value = startingF.toString

    slider.addEventListener(
      "input",
      (_: dom.Event) => updateSynthFrequency()
    )
    sliderContainer.append(label, slider)
    sliderContainer

  private def stopSynthButton(): Element =
    val div = document.createElement("div")
    div.classList.add("button-pad")

    val stopSynth = document.createElement("button")
    stopSynth.classList.add("transport-button")
    stopSynth.textContent = "🛑"
    stopSynth.addEventListener(
      "click",
      (e: dom.MouseEvent) =>
        oscillatorSine.stop()
        oscillatorSaw.stop()
        oscillatorSquare.stop()
        oscillatorTriangle.stop()
    )
    div.appendChild(stopSynth)
    div
  def synthVolumeValue(waveType: WaveType): Double = dom
    .document
    .getElementById(s"${waveType.toString.toLowerCase}-volume")
    .asInstanceOf[html.Input]
    .value
    .toDouble

  def compoundSynthButton(pitch: Pitch): AudioContext ?=> Element =
    val button = document.createElement("button")
    button.textContent = pitch.toString
    button.addEventListener(
      "click",
      (e: dom.MouseEvent) => {

        oscillatorSine.updateFrequency(pitch.calculateFrequency)
        oscillatorSaw.updateFrequency(pitch.calculateFrequency / 4)
        oscillatorSquare.updateFrequency(pitch.calculateFrequency / 2)
        oscillatorTriangle.updateFrequency(pitch.calculateFrequency - 3)

        oscillatorSine.play(synthVolumeValue(Sine))
        oscillatorSaw.play(synthVolumeValue(Sawtooth))
        oscillatorSquare.play(synthVolumeValue(Square))
        oscillatorTriangle.play(synthVolumeValue(Triangle))

        dom
          .window
          .setTimeout(
            () =>
              oscillatorSine.stop()
              oscillatorSaw.stop()
              oscillatorSquare.stop()
              oscillatorTriangle.stop()
            ,
            400)
      }
    )
    button

}

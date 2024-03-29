package org.soundsofscala.testui

import cats.effect.{IO, Ref}
import org.scalajs.dom
import org.scalajs.dom.{AudioContext, Element, document, html}
import org.soundsofscala.synthesis.WaveType.*
import org.soundsofscala.synthesis.{TestSynth, WaveType}
import org.soundsofscala.models.Volume

import scala.concurrent.duration.DurationInt
import cats.effect.unsafe.implicits.global
import org.soundsofscala.testui.CompoundSynthPanel.TestSynthState.*

object CompoundSynthPanel:

  def buildCompoundSynthPanel(): AudioContext ?=> IO[Element] =
    val synth = TestSynth()
    val compoundSynthContainer = document.createElement("div")
    compoundSynthContainer.classList.add("compound-container")
    val compoundSynthLabel = document.createElement("h1")
    val levelsLabel = document.createElement("h1")
    levelsLabel.textContent = "Oscillator Levels"
    val filterLabel = document.createElement("label")
    filterLabel.textContent = "Lowpass Filter"
    val pitcherLabel = document.createElement("label")
    pitcherLabel.textContent = "Pitch Frequency"
    compoundSynthLabel.textContent = "Compound Synth"
    val levelsContainer = document.createElement("div")
    levelsContainer.classList.add("levels-container")
    val levelsDiv = document.createElement("div")
    levelsDiv.classList.add("levels")
    val lowPassContainer = document.createElement("div")
    lowPassContainer.classList.add("levels-container")
    val pitchContainer = document.createElement("div")
    pitchContainer.classList.add("levels-container")
    val pitchFilterDiv = document.createElement("div")
    pitchFilterDiv.classList.add("levels")
    val filterDiv = document.createElement("div")
    filterDiv.classList.add("levels")
    val transport = document.createElement("div")
    transport.classList.add("transport")

    pitchFilterDiv.append(
      pitcherLabel,
      buildPitchSlider(440, synth)
    )
    filterDiv.append(
      filterLabel,
      buildFilterFreqSlider(5000, synth)
    )
    levelsDiv.append(
      levelsLabel,
      buildVolumeSlider(0.1, Sine, synth),
      buildVolumeSlider(0, Sawtooth, synth),
      buildVolumeSlider(0, Triangle, synth),
      buildVolumeSlider(0, Square, synth)
    )

    pitchContainer.appendChild(pitchFilterDiv)
    lowPassContainer.appendChild(filterDiv)
    levelsContainer.appendChild(levelsDiv)

    compoundSynthContainer.append(
      compoundSynthLabel,
      transport,
      pitchContainer,
      lowPassContainer,
      levelsContainer
    )
    for
      playingRef <- Ref.of[IO, TestSynthState](Stopped)
      buttonElement <- compoundSynthPlayButton(synth, playingRef)
      _ = transport.append(buttonElement)
    yield compoundSynthContainer

  enum TestSynthState:
    case Playing, Stopped

  private def compoundSynthPlayButton(synth: TestSynth, playingRef: Ref[IO, TestSynthState])(
      using audioContext: AudioContext): IO[Element] = IO:
    val div = document.createElement("div")
    div.classList.add("play-stop-pad")
    val compoundSynthButton = document.createElement("button")
    compoundSynthButton.id = "compound-synth-start-stop"
    compoundSynthButton.classList.add("start-button-stopped")
    compoundSynthButton.textContent = "►"

    compoundSynthButton.addEventListener(
      "click",
      (_: dom.MouseEvent) =>
        (for
          currentState <- playingRef.get
          _ <- currentState match
            case Playing =>
              playingRef.set(Stopped) >> IO:
                synth.stop()
                compoundSynthButton.textContent = "►"
                compoundSynthButton.classList.remove("start-button-playing")
            case Stopped =>
              playingRef.set(Playing) >> IO:
                synth.play(audioContext.currentTime)
                compoundSynthButton.textContent = "◼︎"
                compoundSynthButton.classList.add("start-button-playing")
                updateSynthVolume(WaveType.Sine, synth)
                updateSynthVolume(WaveType.Sawtooth, synth)
                updateSynthVolume(WaveType.Square, synth)
                updateSynthVolume(WaveType.Triangle, synth)
        yield IO.unit).unsafeRunAndForget()
    )
    div.appendChild(compoundSynthButton)
    div

  private def getSlider(id: String) = dom.document.createElement(id) match
    case sliderElement: html.Input => sliderElement

  private def buildVolumeSlider(startingV: Double, waveType: WaveType, synth: TestSynth) =
    val div = document.createElement("div")
    val label = document.createElement("label")
    label.textContent = s"${waveType.toString}"
    label.classList.add("vol-label")
    div.appendChild(label)
    val slider: html.Input = getSlider("input")
    div.classList.add("slider-pad")
    slider.classList.add("slider-thumb")
    slider.classList.add("volume")

    slider.id = s"${waveType.toString.toLowerCase}-volume"
    slider.`type` = "range"
    slider.min = "0"
    slider.max = "1"
    slider.step = "0.01"
    slider.value = startingV.toString

    def updateSliderBackground(): Unit =
      val min = slider.min.toDouble
      val max = slider.max.toDouble
      val value = slider.valueAsNumber
      val percentage = ((value - min) / (max - min)) * 100
      slider.style.background =
        s"linear-gradient(to right, #00FF00 0%, #00FF00 $percentage%, #D3D3D3FF $percentage%)"

    updateSliderBackground()
    slider.addEventListener(
      "input",
      (_: dom.Event) =>
        updateSynthVolume(waveType, synth)
        updateSliderBackground()
    )
    div.appendChild(slider)
    div

  private def buildFilterFreqSlider(startingF: Int, synth: TestSynth) =
    val sliderContainer = document.createElement("div")
    sliderContainer.classList.add("slider-container")
    val slider = getSlider("input")
    slider.className = "range"
    slider.id = "filter-frequency"
    slider.classList.add("slider-thumb")
    slider.`type` = "range"
    slider.min = "20"
    slider.max = "5000"
    slider.step = "0.1"
    slider.value = startingF.toString

    def updateSliderBackground(): Unit =
      val min = slider.min.toDouble
      val max = slider.max.toDouble
      val value = slider.valueAsNumber
      val percentage = ((value - min) / (max - min)) * 100
      slider.style.background =
        s"linear-gradient(to right, #FF2828FF 0%, #FF2828FF $percentage%, #D3D3D3FF $percentage%)"

    updateSliderBackground()

    slider.addEventListener(
      "input",
      (_: dom.Event) =>
        updateSynthFilterFrequency(synth)
        updateSliderBackground()
    )
    sliderContainer.append(slider)
    sliderContainer

  private def buildPitchSlider(startingF: Int, synth: TestSynth) =
    val sliderContainer = document.createElement("div")
    sliderContainer.classList.add("slider-container")

    val slider = getSlider("input")
    slider.className = "range"
    slider.classList.add("slider-thumb")
    slider.id = "frequency"
    slider.`type` = "range"
    slider.min = "20"
    slider.max = "1000"
    slider.step = "0.01"
    slider.value = startingF.toString

    def updateSliderBackground(): Unit =
      val min = slider.min.toDouble
      val max = slider.max.toDouble
      val value = slider.valueAsNumber
      val percentage = ((value - min) / (max - min)) * 100
      slider.style.background =
        s"linear-gradient(to right, #11ffff 0%, #11ffff $percentage%, #D3D3D3FF $percentage%)"

    updateSliderBackground()

    slider.addEventListener(
      "input",
      (_: dom.Event) =>
        updateSynthFrequency(synth)
        updateSliderBackground()
    )
    sliderContainer.append(slider)
    sliderContainer

  private def retrieveValueFromInputNode(elementId: String): Double =
    dom.document.getElementById(elementId) match
      case input: html.Input => input.value.toDouble
      case _ => 0.0

  private def updateSynthVolume(waveType: WaveType, synth: TestSynth): Unit =
    val volume = retrieveValueFromInputNode(s"${waveType.toString.toLowerCase}-volume")
    synth.updateVolume(Volume(volume), waveType)

  private def updateSynthFilterFrequency(synth: TestSynth): Unit =
    val frequency = retrieveValueFromInputNode("filter-frequency")
    synth.updateFilterFrequency(frequency)

  private def updateSynthFrequency(synth: TestSynth): Unit =
    val frequency = retrieveValueFromInputNode("frequency")
    synth.updatePitchFrequency(frequency)

  def playAGnarlySynthNote(synth: TestSynth)(using audioContext: AudioContext): IO[Unit] =
    for
      _ <- IO(updateSynthFilterFrequency(synth))
      _ <- IO(updateSynthVolume(Sine, synth))
      _ <- IO(updateSynthVolume(Sawtooth, synth))
      _ <- IO(updateSynthVolume(Square, synth))
      _ <- IO(updateSynthVolume(Triangle, synth))
      _ <- IO(synth.play(audioContext.currentTime)) >> IO.sleep(300.millis) >> IO(synth.stop())
    yield IO.unit

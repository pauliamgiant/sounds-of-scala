package org.soundsofscala

import cats.effect.unsafe.implicits.global
import cats.syntax.all.*
import org.scalajs.dom
import org.scalajs.dom.*
import org.scalajs.dom.html.Select
import org.soundsofscala.Instruments.{ScalaSynth, SimpleScala808DrumMachine}
import org.soundsofscala.models.*
import org.soundsofscala.models.AtomicMusicalEvent.*
import org.soundsofscala.songs.*
import org.soundsofscala.syntax.all.*
import org.soundsofscala.synthesis.Oscillator.*
import org.soundsofscala.synthesis.WaveType.{Sawtooth, Sine, Square, Triangle}
import org.soundsofscala.synthesis.{Oscillator, TestSynth, WaveType}
import org.soundsofscala.testui.CompoundSynthPanel
import org.soundsofscala.transport.Sequencer

object Main extends App:

  buildUI()
  private def buildUI(): Unit =
    document.addEventListener(
      "DOMContentLoaded",
      (e: dom.Event) =>

        given audioContext: AudioContext = new AudioContext()

        // allows you to play notes on the keyboard asdfghjkl -> abcdefgab

        dom.document.addEventListener("keydown", key => handleKeyPress(key, TestSynth()))

        given ScalaSynth = ScalaSynth()

        val songs = List[Song](
          TestSong1.demoSong(),
          ChordTestSong1.chordsSong(),
          PolyRhythmicDrums.polyRhythms(),
          DemoTune.demoTune(),
          ChromaticScalaSynthSong.chromaticScalaSynthSong()
        )

        val homeDiv = document.createElement("div")
        homeDiv.classList.add("home-div")

        val heading = document.createElement("h1")
        heading.textContent = "Welcome to Sounds of Scala"

        val logoContainer = document.createElement("div")
        logoContainer.classList.add("logo-container")
        val img = document.createElement("img") match
          case image: HTMLImageElement => image
        img.src = "/resources/images/sounds_of_scala_logo.png"
        img.width = 200
        logoContainer.appendChild(img)

        val exampleWebAppLabel = document.createElement("h2")
        exampleWebAppLabel.textContent = "Example WebApp"

        document.createElement("hr")

        val scalaSynthTitle = document.createElement("h2")
        scalaSynthTitle.textContent = "ScalaSynth"

        val scalaSynthDescription = document.createElement("p")
        scalaSynthDescription.textContent =
          "A basic synthesizer built from WebAudio API components"

        val drumMachineLabel = document.createElement("h2")
        drumMachineLabel.textContent = "SimpleScala808DrumMachine"

        val drumMachineDescription = document.createElement("p")
        drumMachineDescription.textContent = "A basic sample based drum machine"

        val sequencerLabel = document.createElement("h2")
        sequencerLabel.textContent = "Sequencer/Scheduler Example Song"

        val sequencerStop = document.createElement("p")
        sequencerStop.textContent = "🔄 Refresh to stop sequencer"

        val drumSynthLabel = document.createElement("h2")
        drumSynthLabel.textContent = "Drum Synth"

        val drumSynthDescription = document.createElement("p")
        drumSynthDescription.textContent =
          "Using WebAudio API components to re-create classic electronic drum sounds"

        val simpleSineSynthLabel = document.createElement("h1")
        simpleSineSynthLabel.textContent = "Oscillators"

        // Append elements to Document

        CompoundSynthPanel
          .buildCompoundSynthPanel()
          .map: synthPanel =>
            homeDiv.append(
              heading,
              logoContainer,
              exampleWebAppLabel,
              document.createElement("hr"),
              scalaSynthTitle,
              scalaSynthDescription,
              scalaSynthButtonStrip(scalaSynthButton),
              document.createElement("hr"),
              sequencerLabel,
              buildDropDownSongSelecter(songs),
              playSong(songs),
              sequencerStop,
              document.createElement("hr"),
              drumSynthLabel,
              drumSynthDescription,
              synthDrums,
              document.createElement("hr"),
              simpleSineSynthLabel,
              buildDropDownOscillatorSelecter(),
              startStopOsc(),
              document.createElement("hr"),
              drumMachineLabel,
              drumMachineDescription,
              simple808DrumMachineDiv,
              document.createElement("hr"),
              synthPanel
            )
          .unsafeRunAndForget()
        document.body.appendChild(homeDiv)
    )

  private def simple808DrumMachineDiv: AudioContext ?=> Element =
    val div = document.createElement("div")
    div.classList.add("button-pad")
    val drumMachine = SimpleScala808DrumMachine(Tempo(110))
    val sequencerButtonDiv = document.createElement("button")
    sequencerButtonDiv.textContent = "️🥁"
    sequencerButtonDiv.addEventListener(
      "click",
      (_: dom.MouseEvent) =>
        drumMachine
          .playGroove(TestSong1.kick, TestSong1.snare, TestSong1.hats, TestSong1.clap)
          .unsafeRunAndForget()
    )
    div.appendChild(sequencerButtonDiv)
    div

  private def playSong(songs: List[Song]): AudioContext ?=> Element =
    val div = document.createElement("div")
    div.classList.add("button-pad")
    val sequencerButtonDiv = document.createElement("button")
    sequencerButtonDiv.textContent = "️🎼"
    sequencerButtonDiv.addEventListener(
      "click",
      (_: dom.MouseEvent) =>
        val sequencer = Sequencer()
        val song: Song = document.getElementById("songs") match
          case select: Select =>
            songs
              .collectFirst { case song if song.title.value === select.value => song }
              .getOrElse(songs.head)
        sequencer.playSong(song).unsafeRunAndForget()
    )
    div.appendChild(sequencerButtonDiv)
    div
  end playSong

  private def synthDrums: AudioContext ?=> Element =
    val div = document.createElement("div")
    div.classList.add("button-pad")
    val sequencerButtonDiv = document.createElement("button")
    sequencerButtonDiv.textContent = "️🛢️"
    sequencerButtonDiv.addEventListener(
      "click",
      (_: dom.MouseEvent) =>
        val sequencer = Sequencer()
        sequencer.playSong(DrumSynthTestSong.drumSynthSong()).unsafeRunAndForget()
    )
    div.appendChild(sequencerButtonDiv)
    div

  private def buildDropDownSongSelecter(songs: List[Song]): AudioContext ?=> Element =

    val div = document.createElement("div")
    div.classList.add("button-pad")

    val select: html.Select = document.createElement("select") match
      case selectElement: html.Select => selectElement

    select.id = "songs"
    select.classList.add("songs")
    val options = songs.map(songSelectionOptions)
    select.append(options*)
    div.appendChild(select)
    div

  private def buildDropDownOscillatorSelecter(): Element =

    val waveTypes = List(
      Sine,
      Sawtooth,
      Square,
      Triangle
    )
    val div = document.createElement("div")
    div.classList.add("button-pad")

    val select: html.Select = document.createElement("select") match
      case selectElement: html.Select => selectElement

    select.addEventListener(
      "change",
      (_: dom.Event) =>
        val waveType = Oscillator.stringToWaveType(
          document.getElementById("oscillator") match
            case select: Select => select.value
        )
        val button = document.getElementById("oscillator-start-stop") match
          case button: html.Button => button
        button.innerHTML =
          s"<img src='/resources/images/${waveType.toString.toLowerCase}.svg' alt='Button Image'>"
    )
    select.id = "oscillator"
    select.classList.add("oscillator")
    val options = waveTypes.map(waveSelectionOptions)
    select.append(options*)
    div.appendChild(select)
    div

  private def waveSelectionOptions(waveType: WaveType): Element =
    val option = document.createElement("option")
    option.textContent = waveType.toString
    option

  private def songSelectionOptions(song: Song): Element =
    val option = document.createElement("option")
    option.textContent = song.title.value
    option

  def scalaSynthButtonStrip(buttonBuilder: Note => Element): Element =
    val buttonPad = document.createElement("div")
    buttonPad.classList.add("button-pad")

    val buttonList = List[Note](
      C3,
      D3,
      E3,
      F3,
      G3,
      A3,
      B3
    ).map(buttonBuilder)

    buttonPad.append(buttonList*)
    buttonPad

  def startStopOsc()(using audioContext: AudioContext): Element =
    val div = document.createElement("div")
    div.classList.add("wav-button-pad")
    val button = document.createElement("button")
    button.classList.add("osc-button")
    button.id = "oscillator-start-stop"
    button.innerHTML = "<img src='/resources/images/sine.svg' alt='Button Image'>"
    button.addEventListener(
      "click",
      (_: dom.MouseEvent) =>
        val waveType = Oscillator.stringToWaveType(
          document.getElementById("oscillator") match
            case select: Select => select.value
        )
        val oscillator = audioContext.createOscillator()
        oscillator.`type` = waveType.toString.toLowerCase
        val gain = audioContext.createGain()
        gain.gain.value = 0.1
        oscillator.connect(gain)
        oscillator.start()
        gain.connect(audioContext.destination)
        window.setTimeout(() => oscillator.stop(), 1000)
//
    )
    div.appendChild(button)
    div

  def scalaSynthButton(note: Note)(using synth: ScalaSynth)(using ac: AudioContext): Element =
    val button = document.createElement("button")
    button.textContent = note.pitch.toString
    button.addEventListener(
      "click",
      (_: dom.MouseEvent) =>
        synth.attackRelease(ac.currentTime, note, Tempo(120), Release(1)).unsafeRunAndForget()
    )
    button

  private val keyToSemitoneOffset: Map[String, Int] = Map(
    "a" -> 0, // A
    "w" -> 1, // A#/Bb
    "s" -> 2, // B
    "d" -> 3, // C
    "r" -> 4, // C#/Db
    "f" -> 5, // D
    "t" -> 6, // D#/Eb
    "g" -> 7, // E
    "h" -> 8, // F
    "u" -> 9, // F#/Gb
    "j" -> 10, // G
    "i" -> 11, // G#/Ab
    "k" -> 12, // A
    "o" -> 13, // A#/Bb
    "l" -> 14, // B
    "p" -> 15 // C
  )

  private def handleKeyPress(keyEvent: KeyboardEvent, testSynth: TestSynth)(
      using audioContext: AudioContext): Unit =

    val offsetFromReference: Option[Int] = keyToSemitoneOffset.get(keyEvent.key.toLowerCase())
    offsetFromReference.foreach: offset =>
      val frequency = 220 * Math.pow(2, offset / 12.0)
      testSynth.updatePitchFrequency(frequency)
      CompoundSynthPanel.playAGnarlySynthNote(testSynth).unsafeRunAndForget()

    keyEvent.key match
      case " " =>
        val button: HTMLButtonElement =
          document.getElementById("compound-synth-start-stop") match
            case but: HTMLButtonElement => but
        button.click()
      case _ => ()

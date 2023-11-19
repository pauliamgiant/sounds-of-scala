package com.soundsofscala

import com.soundsofscala.models.*
import com.soundsofscala.models.Accidental.*
import com.soundsofscala.models.Duration.*
import com.soundsofscala.models.MusicalEvent.*
import com.soundsofscala.models.Pitch.*
import com.soundsofscala.models.Velocity.*
import io.github.iltotore.iron.{:|, IronType, autoRefine}
import org.scalajs.dom
import org.scalajs.dom.document

@main def main(): Unit =
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
      songTitle.textContent = s"First three notes:"
      homeDiv.appendChild(songTitle)

      val firstSong = document.createElement("h2")
      firstSong.textContent = threeNoteMelody()
      homeDiv.appendChild(firstSong)
  )

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

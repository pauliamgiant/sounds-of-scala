package com.soundsofscala.synthesis

import com.soundsofscala.Main.loadAudioSample
import org.scalajs.dom
import org.scalajs.dom.{AudioContext, window}

import scala.collection.mutable
import scala.scalajs.js

case class Scheduler() {
  val tempo = 100.0;
  val lookahead = 25.0
  val scheduleAheadTime = 0.1
  var currentNote = 0
  var nextNoteTime = 0.0
  var noteQueue: mutable.Queue[NoteTime] = mutable.Queue.empty
  var timerID = 0

  def go(path: String, noteLength: Double)(using audioContext: AudioContext): Unit = {
    currentNote = 0
    nextNoteTime = audioContext.currentTime
//    scheduler("resources/audio/drums/Hats808.wav", 0.5)
    scheduler(path, noteLength)
  }

  def scheduler(path: String, noteLength: Double)(using audioContext: AudioContext): Unit = {
    // While there are notes that will need to play before the next interval,
    // schedule them and advance the pointer.
    while (nextNoteTime < audioContext.currentTime + scheduleAheadTime) {
      scheduleNote(currentNote, nextNoteTime, path)
      nextNote(noteLength)
    }
    timerID = window.setTimeout(() => scheduler(path, noteLength), lookahead)
//    setTimeout(lookahead,scheduler())
  }

  case class NoteTime(beatNumber: Int, time: Double)

  def scheduleNote(beatNumber: Int, time: Double, path: String)(
      using audioContext: AudioContext) =
    println("Scheduling note at " + time + " for beat " + beatNumber + " with path " + path)
    noteQueue.enqueue(NoteTime(beatNumber, time))
    playDrum(time, path)

  def nextNote(noteLength: Double) = {
    val secondsPerBeat = 60.0 / tempo

    nextNoteTime += noteLength * secondsPerBeat
    currentNote += 1
    if (currentNote == 4) {
      currentNote = 0
    }
  }

  def playDrum(time: Double, filePath: String)(using audioContext: AudioContext) = {
    println(s"Playing note at $time")

    loadAudioSample(
      filePath,
      audioContext,
      buffer => {
        val gainNode = audioContext.createGain()
        gainNode.gain.value = 0.5
        val sourceNode = audioContext.createBufferSource()
        sourceNode.buffer = buffer
        sourceNode.connect(gainNode)
        gainNode.connect(audioContext.destination)
        sourceNode.start(time)
      }
    )
  }
}

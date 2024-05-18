package org.soundsofscala.songexamples

import cats.effect.IO
import org.scalajs.dom.AudioContext
import org.soundsofscala.models.Song

trait SongExample:
  def apply(): AudioContext ?=> Song = song()

  def song(): AudioContext ?=> Song

  def play(): AudioContext ?=> IO[Unit] = song().play()

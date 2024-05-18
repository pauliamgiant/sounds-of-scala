package org.soundsofscala.models

import cats.data.NonEmptyList
import cats.effect.IO
import org.scalajs.dom.AudioContext
import org.soundsofscala.transport.Sequencer

case class Song(
    title: Title,
    tempo: Tempo = Tempo(120),
    swing: Swing = Swing(0),
    mixer: Mixer
):
  def play()(using AudioContext): IO[Unit] =
    IO.println(s"Playing: $title") *> Sequencer().playSong(this)

case class Mixer(tracks: NonEmptyList[Track])
object Mixer:
  def apply(tracks: Track*): Mixer = Mixer(
    NonEmptyList(tracks.head, tracks.tail.toList)
  )

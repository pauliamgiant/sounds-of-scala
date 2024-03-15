package org.soundsofscala.models

import cats.data.NonEmptyList
import org.soundsofscala.Instruments.Instrument

case class Song(
    title: Title,
    tempo: Tempo = Tempo(120),
    swing: Swing = Swing(0),
    mixer: Mixer
)

case class Track(title: Title, musicalEvent: MusicalEvent, instrument: Instrument)

case class Mixer(tracks: NonEmptyList[Track])
object Mixer:
  def apply(tracks: Track*): Mixer = Mixer(
    NonEmptyList(tracks.head, tracks.tail.toList)
  )

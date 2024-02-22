package com.soundsofscala.models

import cats.data.NonEmptyList
import com.soundsofscala.models.Voice

case class Song(
    title: Title,
    tempo: Tempo = Tempo(120),
    swing: Swing = Swing(0),
    voices: AllVoices
)

//case class Voice(voice: ChannelVoices)

case class AllVoices(voices: NonEmptyList[Voice])
object AllVoices {
  def apply(voices: Voice*): AllVoices = AllVoices(
    NonEmptyList(voices.head, voices.tail.toList)
  )
}

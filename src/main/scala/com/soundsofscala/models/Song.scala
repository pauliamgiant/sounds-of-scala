package com.soundsofscala.models

import cats.data.NonEmptyList
import com.soundsofscala.models.Voice
import refined4s.Newtype
import refined4s.Refined

type Title = Title.Type
object Title extends Newtype[String]

type Tempo = Tempo.Type
object Tempo extends Refined[Double]:
  override inline def invalidReason(tempo: Double): String =
    expectedMessage(
      "is an Int between 1 and 300. If you want to exceed 300 you need to re-think your life.")
  override inline def predicate(tempo: Double): Boolean =
    33 <= tempo && tempo <= 300

type Swing = Swing.Type
object Swing extends Refined[Int]:
  override inline def invalidReason(s: Int): String =
    expectedMessage("is an Int between 0 and 10. 0 is totally straight. 10 is very swung.")
  override inline def predicate(s: Int): Boolean =
    0 <= s && s <= 10

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

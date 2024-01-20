package com.soundsofscala.models

enum Voice(musicalEvent: MusicalEvent):
  val sequence: MusicalEvent = musicalEvent
  case KickChannel(musicalEvent: MusicalEvent) extends Voice(musicalEvent)
  case SnareChannel(musicalEvent: MusicalEvent) extends Voice(musicalEvent)
  case HatsChannel(musicalEvent: MusicalEvent) extends Voice(musicalEvent)
  case PianoChannel(musicalEvent: MusicalEvent) extends Voice(musicalEvent)

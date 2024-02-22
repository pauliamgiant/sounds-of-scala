package com.soundsofscala

import com.soundsofscala.models.*
import com.soundsofscala.models.AtomicMusicalEvent.*
import com.soundsofscala.models.DrumVoice.*
import com.soundsofscala.models.Duration.*
import com.soundsofscala.models.Velocity.*

object TestData {
  val quarterKickDrum: DrumStroke = DrumStroke(Kick, Quarter, OnFull)
  val quarterSnareDrum: DrumStroke = DrumStroke(Snare, Quarter, OnFull)
  val RockBeatOne: Any =
    quarterKickDrum + quarterSnareDrum + quarterKickDrum + quarterSnareDrum

}

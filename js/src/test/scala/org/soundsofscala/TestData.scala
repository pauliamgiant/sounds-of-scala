package org.soundsofscala

import org.soundsofscala.models.*
import org.soundsofscala.models.AtomicMusicalEvent.*
import org.soundsofscala.models.DrumVoice.*
import org.soundsofscala.models.Duration.Quarter
import org.soundsofscala.models.Velocity.OnFull

object TestData:
  val quarterKickDrum: DrumStroke = DrumStroke(Kick, Quarter, OnFull)
  val quarterSnareDrum: DrumStroke = DrumStroke(Snare, Quarter, OnFull)
  val RockBeatOne: Any =
    quarterKickDrum + quarterSnareDrum + quarterKickDrum + quarterSnareDrum

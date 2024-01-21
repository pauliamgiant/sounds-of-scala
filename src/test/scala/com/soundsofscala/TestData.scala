package com.soundsofscala

import com.soundsofscala.models.MusicalEvent.*
import models.*
import models.MusicalEvent.*
import models.Accidental.*
import models.Pitch.*
import models.Duration.*
import models.Velocity.*
import models.DrumVoice.*

object TestData:
  val quarterKickDrum: DrumStroke = DrumStroke(Kick, Quarter, OnFull)
  val quarterSnareDrum: DrumStroke = DrumStroke(Snare, Quarter, OnFull)
  val RockBeatOne: Any =
    quarterKickDrum + quarterSnareDrum + quarterKickDrum + quarterSnareDrum

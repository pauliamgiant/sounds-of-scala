package org.soundsofscala.models

import org.soundsofscala.Instruments.Instrument

case class Track(
    title: Title,
    musicalEvent: MusicalEvent,
    instrument: Instrument,
    insertFX: List[FX] = List.empty,
    sendFX: List[FX] = List.empty)

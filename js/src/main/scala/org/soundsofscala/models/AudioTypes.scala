package org.soundsofscala.models

object AudioTypes:
  enum WaveType:
    case Sine, Square, Sawtooth, Triangle

  enum FilterModel:
    case LowPass, HighPass, BandPass, LowShelf, HighShelf, Peaking, Notch, AllPass
    override def toString: String = this.toString.toLowerCase

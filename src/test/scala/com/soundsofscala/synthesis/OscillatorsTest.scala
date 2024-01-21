package com.soundsofscala.synthesis

import com.soundsofscala.synthesis.Oscillator.SawtoothOscillator
import org.scalajs.dom.AudioContext
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class OscillatorsTest extends AnyFunSuite with Matchers with BeforeAndAfterAll:

  test("sawtooth wave"):
    given ac: AudioContext = new AudioContext() // Not finding this
//    val sawtooth = SawtoothOscillator(110)

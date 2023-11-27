package com.soundsofscala.synthesis

import com.soundsofscala.synthesis.Oscillator.SawtoothOscillator
import org.scalajs.dom.AudioContext
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class OscillatorsTest extends AnyFunSuite with Matchers with BeforeAndAfterAll:

  // This will be executed before all tests
  override def beforeAll(): Unit = {
    // Perform setup code here, for example, creating an AudioContext
  }

  // This will be executed after all tests
  override def afterAll(): Unit = {
    // Perform cleanup code here
  }

  test("sawtooth wave") {
    given ac: AudioContext = new AudioContext() // Not finding this
//    val sawtooth = SawtoothOscillator(110)
  }

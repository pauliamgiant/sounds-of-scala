package org.soundsofscala

import org.scalajs.dom.AudioContext

import scala.scalajs.js

object TestUtils:
  def stubAudioContext: AudioContext =
    js.Dynamic.literal(currentTime = 0.0).asInstanceOf[AudioContext]

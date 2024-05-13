package org.soundsofscala.graph

import org.scalajs.dom

final case class AudioParam(events: Vector[AudioParam.AudioParamEvent]):
  import AudioParam.AudioParamEvent

  /**
   * Set all the values in this AudioParam to the given dom.AudioParam
   */
  def set(target: dom.AudioParam): Unit =
    events.foreach(evt =>
      evt match
        case AudioParamEvent.SetValueAtTime(value, startTime) =>
          target.setValueAtTime(value, startTime)
    )

  def setValueAtTime(value: Double, startTime: Double): AudioParam =
    this.copy(events = events :+ AudioParamEvent.SetValueAtTime(value, startTime))

object AudioParam:
  val empty: AudioParam = AudioParam(Vector.empty)

  enum AudioParamEvent:
    case SetValueAtTime(value: Double, startTime: Double)

package org.soundsofscala.graph

import org.scalajs.dom
import org.soundsofscala.graph.AudioParam.AudioParamEvent.*

final case class AudioParam(events: Vector[AudioParam.AudioParamEvent]):
  import AudioParam.AudioParamEvent

  /**
   * Set all the values in this AudioParam to the given dom.AudioParam
   */
  def set(target: dom.AudioParam): Unit =
    events.foreach:
      case SetValueAtTime(value, startTime) =>
        target.setValueAtTime(value, startTime)
      case LinearRampToValueAtTime(value, endTime) =>
        target.linearRampToValueAtTime(value, endTime)
      case ExponentialRampToValueAtTime(value, endTime) =>
        target.exponentialRampToValueAtTime(value, endTime)

  def setValueAtTime(value: Double, startTime: Double): AudioParam =
    this.copy(events = events :+ AudioParamEvent.SetValueAtTime(value, startTime))

  def exponentialRampToValueAtTime(value: Double, endTime: Double): AudioParam =
    this.copy(events = events :+ AudioParamEvent.ExponentialRampToValueAtTime(value, endTime))

  def linearRampToValueAtTime(value: Double, endTime: Double): AudioParam =
    this.copy(events = events :+ AudioParamEvent.LinearRampToValueAtTime(value, endTime))

object AudioParam:
  val empty: AudioParam = AudioParam(Vector.empty)

  enum AudioParamEvent:
    case SetValueAtTime(value: Double, startTime: Double)
    case LinearRampToValueAtTime(value: Double, endTime: Double)
    case ExponentialRampToValueAtTime(value: Double, endTime: Double)

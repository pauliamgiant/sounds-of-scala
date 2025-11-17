package org.soundsofscala.instrument

import cats.effect.{IO, Ref}
import org.scalajs.dom.{AudioContext, AudioNode}
import org.soundsofscala.graph.AudioNode.{Filter, Gain, sineOscillator, waveTableOscillator}
import org.soundsofscala.graph.AudioParam
import org.soundsofscala.graph.AudioParam.AudioParamEvent.*
import org.soundsofscala.models
import org.soundsofscala.models.AtomicMusicalEvent.Note
import org.soundsofscala.models.*
import org.soundsofscala.models.AudioTypes.FilterModel
import org.soundsofscala.graph.AudioParam.+

import scala.scalajs.js

object ViolinSynth:
  def apply()(using audioContext: AudioContext): IO[ViolinSynth] =
    for
      activeNodesRef <- Ref.of[IO, Set[AudioNode]](Set.empty)
    yield new ViolinSynth(activeNodesRef)

final class ViolinSynth private (
    protected val activeNodesRef: Ref[IO, Set[AudioNode]]
)(using audioContext: AudioContext)
    extends Synth:

  override def attackRelease(
      when: Double,
      note: Note,
      tempo: Tempo,
      attack: Attack,
      release: Release,
      pan: Double,
      volume: Double): IO[Unit] =
    for
      createdNodes <- IO {
        val realArray = new js.typedarray.Float32Array(js.Array(
          0,
          0.8f,
          0.6f,
          0.4f,
          0.3f,
          0.2f,
          0.15f
        ))
        val imaginaryArray = new js.typedarray.Float32Array(js.Array(
          0,
          0.05f,
          0.03f,
          0.02f,
          0.015f,
          0.01f,
          0.005f
        ))
        val velocity = note.velocity.getNormalisedVelocity
        val velocityModulatedVolume = volume * velocity * 0.45
        val attackTime = 0.100
        val sustainLevel = velocityModulatedVolume * 0.8
        val durationSeconds = note.durationToSeconds(tempo)
        val releaseTime = 0.5

        val lfo = sineOscillator(
          when = when,
          duration = durationSeconds + releaseTime
        ).withFrequency(AudioParam(Vector(SetValueAtTime(15, when))))

        val lfoGainNode = Gain(
          List.empty,
          AudioParam(Vector(SetValueAtTime(4.0, when)))
        )

        val wavetableOsc =
          waveTableOscillator(
            when = when,
            duration = durationSeconds + releaseTime,
            realArray = realArray,
            imaginaryArray = imaginaryArray
          )
            .withFrequency(
              AudioParam(Vector(SetValueAtTime(note.frequency, when)))
            )

        val filter = Filter(
          List(wavetableOsc),
          AudioParam(Vector(SetValueAtTime(1200, when))),
          AudioParam(Vector(SetValueAtTime(1.5, when))),
          FilterModel.BandPass)

        val gainNode =
          Gain(
            List.empty,
            AudioParam(Vector(
              SetValueAtTime(0.0001, when),
              ExponentialRampToValueAtTime(velocityModulatedVolume, when + attackTime),
              LinearRampToValueAtTime(sustainLevel, when + (attackTime + 0.100)),
              ExponentialRampToValueAtTime(0.0001, when + durationSeconds + releaseTime)
            ))
          )

        val lfoNew = lfo --> lfoGainNode

        val audioGraph = lfoNew --> (audioParam =>
          wavetableOsc.copy(frequency =
            wavetableOsc.frequency + audioParam)) --> filter --> gainNode

        val finalNode = audioGraph.create

        finalNode.connect(audioContext.destination)
        finalNode
      }
      _ <- activeNodesRef.update(oldNodes =>
        oldNodes + createdNodes)
    yield ()
end ViolinSynth

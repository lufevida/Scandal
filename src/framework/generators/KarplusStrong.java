package framework.generators;

import framework.effects.Delay;
import framework.effects.RingModulator;
import framework.utilities.Settings;
import framework.waveforms.WavetableCosine;
import framework.waveforms.WavetableWhite;

public class KarplusStrong extends PolyphonicSynthesizer {

	private final RingModulator tremolo = new RingModulator(new WavetableCosine());
	public float tremoloSpeed = 4;
	private final Delay delay = new Delay();
	public int delayTime = 500;

	public KarplusStrong() throws Exception {
		super(new WavetableWhite());
		attackSamples = decaySamples = releaseSamples = 44;
		sustainLevel = 1.0f;
	}

	@Override
	public void fillMidiNotesArray() {
		for (int i = 0; i < 128; i++) midiNotes.add(new KarplusStrongNote(i));
	}

	class KarplusStrongNote extends PolyphonicSynthesizer.MidiNote {

		int index = 0;
		int nextIndex;
		final float delaySamples = (float) Math.ceil(Settings.samplingRate / frequency);
		final float[] circularBuffer = new float[(int) delaySamples];
		float feedback = 0.996f;

		KarplusStrongNote(int midiNoteNumber) {
			super(midiNoteNumber);
			reset();
		}

		void reset() {
			for (int i = 0; i < circularBuffer.length; i++)
				circularBuffer[i] = baseWavetable.getSample(0, 0);
		}

		@Override
		float[] get() {
			for (int i = 0; i < vector.length; i++) {
				vector[i] = circularBuffer[index] * envelopeLevel * amplitude;
				nextIndex = (index + 1) % circularBuffer.length;
				circularBuffer[index] += circularBuffer[nextIndex];
				circularBuffer[index] *= 0.5 * feedback;
				index = nextIndex;
				updateEnvelope();
				if (envelopeStage == ADSR.OFF) reset();
			}
			return vector;
		}

	}

	@Override
	public void processMasterEffects() {
		mixVector = tremolo.processVector(mixVector, 0.99f, tremoloSpeed);
		mixVector = delay.processVector(mixVector, delayTime, 0.5f, 0.5f);
	}

	@Override
	public void handleModulationWheelChange(int value, int channel) {
		tremoloSpeed = 0.5f * value;
	}

}

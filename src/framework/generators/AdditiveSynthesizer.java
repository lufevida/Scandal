package framework.generators;

import java.util.ArrayList;

import framework.effects.Granulator;
import framework.waveforms.Wavetable;

public class AdditiveSynthesizer extends PolyphonicSynthesizer {

	public static enum Series { LYMAN, BALMER, PASCHEN, BRACKETT, PFUND, HUMPHREYS }
	private Series series;
	private int harmonicCount;
	float n1;
	float n2;
	private final Granulator granulator = new Granulator();
	public float granulatorMix = 1;
	
	public AdditiveSynthesizer(int controller, Wavetable baseWavetable) throws Exception {
		super(controller, baseWavetable);
		this.series = Series.LYMAN;
		this.harmonicCount = 4;
		attackSamples = 882;
		decaySamples = 2205;
		sustainLevel = 0.5f;
		releaseSamples = 22050;
		for (MidiNote note : midiNotes) ((AdditiveNote) note).init();
	}

	public AdditiveSynthesizer(int controller, Wavetable baseWavetable, Series series, int harmonicCount) throws Exception {
		super(controller, baseWavetable);
		this.series = series;
		this.harmonicCount = harmonicCount;
		for (MidiNote note : midiNotes) ((AdditiveNote) note).init();
	}
	
	public void setSeries(Series series) {
		this.series = series;
		for (MidiNote note : midiNotes) ((AdditiveNote) note).reset();
	}
	
	public void setHarmonicCount(int count) {
		harmonicCount = count;
		for (MidiNote note : midiNotes) ((AdditiveNote) note).reset();
	}
	
	@Override
	public void fillMidiNotesArray() {
		for (int i = 0; i < 128; i++) midiNotes.add(new AdditiveNote(i));
	}
	
	class AdditiveNote extends PolyphonicSynthesizer.MidiNote {
		
		ArrayList<Float> phases = new ArrayList<Float>();
		ArrayList<Float> increments = new ArrayList<Float>();

		AdditiveNote(int midiNoteNumber) {
			super(midiNoteNumber);
		}
		
		void init() {
			n1 = series.ordinal() + 1.0f;
			for (int i = 0; i < harmonicCount; i++) {
				n2 = n1 + i + 1.0f;
				phases.add(i, 0.0f);
				increments.add(i, n1 * n1 * phaseIncrement * (1.0f / (n1 * n1) - 1.0f / (n2 * n2)));
			}
		}
		
		void reset() {
			n1 = series.ordinal() + 1.0f;
			for (int i = 0; i < harmonicCount; i++) {
				n2 = n1 + i + 1.0f;
				phases.set(i, 0.0f);
				increments.set(i, n1 * n1 * phaseIncrement * (1.0f / (n1 * n1) - 1.0f / (n2 * n2)));
			}
		}
		
		@Override
		float[] get() {
			for (int i = 0; i < vector.length; i++) {
				vector[i] = 0.0f;
				for (int j = 0; j < harmonicCount; j++) {
					vector[i] += baseWavetable.getSample(phases.get(j), increments.get(j));
					phases.set(j, phases.get(j) + increments.get(j));
					if (phases.get(j) >= baseWavetable.tableSize)
						phases.set(j, phases.get(j) - baseWavetable.tableSize);
				}
				vector[i] += baseWavetable.getSample(phase, phaseIncrement);
				vector[i] *= envelopeLevel * amplitude / harmonicCount;
				phase += phaseIncrement;
				if (phase >= baseWavetable.tableSize) phase -= baseWavetable.tableSize;
				updateEnvelope();
				if (envelopeStage == ADSR.OFF)
					for (int k = 0; k < harmonicCount; k++) phases.set(k, 0.0f);
			}
			return vector;
		}
		
	}
	
	@Override
	public void processMasterEffects() {
		mixVector = granulator.processVector(mixVector, granulatorMix);
	}
	
	@Override
	public void handleModulationWheelChange(int value, int channel) {
		granulatorMix = (float) value / 127.0f;
	}

}

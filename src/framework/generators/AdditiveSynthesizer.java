package framework.generators;

import java.util.ArrayList;

import framework.effects.Granulator;
import framework.waveforms.Wavetable;

public class AdditiveSynthesizer extends PolyphonicSynthesizer {
	
	private static interface Lambda {
		float apply(float x);
	}
	
	private static float inverseSquare(float x) {
		return 1 / (x * x);
	}

	public static enum Series {
		LYMAN(x -> 1 - inverseSquare(x + 1)),
		BALMER(x -> 4 * (inverseSquare(2) - inverseSquare(x + 2))),
		PASCHEN(x -> 9 * (inverseSquare(3) - inverseSquare(x + 3))),
		BRACKETT(x -> 16 * (inverseSquare(4) - inverseSquare(x + 4))),
		PFUND(x -> 25 * (inverseSquare(5) - inverseSquare(x + 5))),
		HUMPHREYS(x -> 36 * (inverseSquare(6) - inverseSquare(x + 6))),
		BUZZ(x -> x + 1),
		BELL(x -> (x + 1) + (float) Math.random()),
		RANDOM(x -> (x + 1) * (float) Math.random());
		
		Lambda lambda;
		
		Series(Lambda lambda) {
			this.lambda = lambda;
		}
	}
	
	private Series series;
	private int harmonicCount;
	public final Granulator granulator = new Granulator();
	
	public AdditiveSynthesizer(Wavetable baseWavetable) throws Exception {
		super(baseWavetable);
		this.series = Series.BELL;
		this.harmonicCount = 16;
		attackSamples = 882;
		decaySamples = 2205;
		sustainLevel = 0.5f;
		releaseSamples = 22050;
		for (MidiNote note : midiNotes) ((AdditiveNote) note).init();
	}

	public AdditiveSynthesizer(Wavetable baseWavetable, Series series, int harmonicCount) throws Exception {
		super(baseWavetable);
		this.series = series;
		this.harmonicCount = harmonicCount;
		for (MidiNote note : midiNotes) ((AdditiveNote) note).init();
	}
	
	public void setSeries(Series series, int count) {
		this.series = series;
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
			for (int i = 1; i <= harmonicCount; i++) {
				phases.add(i - 1, 0.0f);
				increments.add(i - 1, phaseIncrement * series.lambda.apply(i));
			}
		}
		
		void reset() {
			for (int i = 1; i <= harmonicCount; i++) {
				phases.set(i - 1, 0.0f);
				increments.set(i - 1, phaseIncrement * series.lambda.apply(i));
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
		mixVector = granulator.processVector(mixVector);
	}
	
	@Override
	public void handleModulationWheelChange(int value, int channel) {
		granulator.mix = (float) value / 127.0f;
	}

}

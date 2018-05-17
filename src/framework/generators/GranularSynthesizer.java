package framework.generators;

import java.util.ArrayList;

import framework.waveforms.Wavetable;
import framework.waveforms.WavetableCosine;

public class GranularSynthesizer extends PolyphonicSynthesizer {
	
	public int playbackPosition = 8820;
	public int playbackDeviation = 4410;
	public float playbackSpeed = 1;
	public int grainLength = 8820;
	public int interGrainTime = 441;
	public int grainCount = 256;
	public int windowSize = 8192;
	public double windowIncrement = (double) windowSize / grainLength;
	public float[] window = new WindowFunction(windowSize).get();
	private final float[] array;
	private final float baseFrequency;

	public GranularSynthesizer(Wavetable baseWavetable) throws Exception {
		super(baseWavetable);
		this.array = null;
		this.baseFrequency = 1;
		attackSamples = 22050;
		sustainLevel = 1;
		releaseSamples = 44100;
		for (MidiNote note : midiNotes) ((GranularNote) note).init();
	}
	
	public GranularSynthesizer(float[] array, float baseFrequency) throws Exception {
		super(new WavetableCosine());
		this.array = array;
		this.baseFrequency = baseFrequency;
		attackSamples = 22050;
		sustainLevel = 1;
		releaseSamples = 44100;
		for (MidiNote note : midiNotes) ((GranularNote) note).init();
	}
	
	@Override
	public void fillMidiNotesArray() {
		for (int i = 0; i < 128; i++) midiNotes.add(new GranularNote(i));
	}
	
	class GranularNote extends PolyphonicSynthesizer.MidiNote {
		
		ArrayList<Grain> grainArray = new ArrayList<>();
		int igtCounter = 0;
		int grainArrayCounter = 0;
		
		class Grain {
			
			boolean isBusy;
			private float grainPhase;
			private float speedCorrection;
			private float sample;
			private double windowIndex;
			
			Grain() {
				reset();
			}
			
			void reset() {
				isBusy = false;
				grainPhase = playbackPosition + (((float) Math.random() * 2 - 1) * playbackDeviation);
				while (grainPhase >= baseWavetable.tableSize) grainPhase -= baseWavetable.tableSize;
				if (grainPhase < 0) grainPhase += baseWavetable.tableSize;
				windowIndex = 0;
				speedCorrection = frequency / baseFrequency;
			}
			
			float getSample() {
				if (array != null) {
					sample = array[(int) grainPhase] * window[(int) windowIndex];
					grainPhase += playbackSpeed * speedCorrection;
					if (grainPhase >= array.length) grainPhase -= array.length;
				}
				else {
					sample = baseWavetable.getSample(grainPhase, phaseIncrement) * window[(int) windowIndex] * 0.1f;
					grainPhase += phaseIncrement * playbackSpeed;
					if (grainPhase >= baseWavetable.tableSize) grainPhase -= baseWavetable.tableSize;
				}
				windowIndex += windowIncrement;
				if (windowIndex >= windowSize) reset();
				return sample;
			}
			
		}

		GranularNote(int midiNoteNumber) {
			super(midiNoteNumber);
		}
		
		/*
		 * The GranularNote constructor is called at the moment the PolyphonicSynthesizer superclass
		 * is constructed, hence before the field grainCount has been initialized. So we have to wait
		 * until the superclass has been constructed if we need to use here any property belonging
		 * exclusively to the GranularSynthesizer subclass, thus the need for an extra init method.
		 */
		void init() {
			for (int i = 0; i < grainCount; i++) grainArray.add(i, new Grain());
			grainArray.get(0).isBusy = true;
		}
		
		@Override
		float[] get() {
			for (int i = 0; i < vector.length; i++) {
				vector[i] = 0;
				for (Grain grain : grainArray) if (grain.isBusy) vector[i] += grain.getSample();
				vector[i] *= envelopeLevel;
				updateGrainArray();
				updateEnvelope();
			}
			if (envelopeStage == ADSR.OFF) {
				for (Grain grain : grainArray) if (grain.isBusy) grain.reset();
				grainArray.get(0).isBusy = true;
				igtCounter = 0;
				grainArrayCounter = 0;
			}
			return vector;
		}
		
		@Override
		void updateVelocity(int velocity) {
			if (velocity != 0) {
				amplitude = (float) velocity / 127;
				envelopeStage = ADSR.ATTACK;
				envelopeSamples = attackSamples;
				envelopeSlope = (amplitude - envelopeLevel) / attackSamples;
			} else {
				envelopeStage = ADSR.RELEASE;
				envelopeSamples = releaseSamples;
				envelopeSlope = envelopeLevel / releaseSamples;
			}
		}
		
		void updateGrainArray() {
			igtCounter++;
			if (igtCounter >= interGrainTime) {
				igtCounter = 0;
				grainArrayCounter++;
				if (grainArrayCounter >= grainCount) grainArrayCounter = 0;
				grainArray.get(grainArrayCounter).isBusy = true;
			}
		}
		
	}

}

package framework.generators;

import java.nio.ByteBuffer;
import java.util.Arrays;

import framework.utilities.Settings;
import framework.waveforms.Wavetable;

public class WavetableOscillator implements RealTimePerformer {

	public float amplitude;
	private float frequency;
	private float runningPhase = 0;
	public final float frequencyScale;
	public final Wavetable wavetable;
	public float[] mixVector = new float[Settings.vectorSize];
	private final ByteBuffer buffer = ByteBuffer.allocate(Settings.vectorSize * Settings.bitDepth / 8);
	private final AudioFlow flow = new AudioFlow(this, Settings.mono);

	public WavetableOscillator(Wavetable wavetable) {
		this.wavetable = wavetable;
		frequencyScale = (float) wavetable.tableSize / Settings.samplingRate;
	}

	public float[] get(int duration, float amplitude, float frequency) {
		int samples = duration * Settings.samplingRate / 1000;
		float oscFreq = frequency * frequencyScale;
		float oscPhase = 0;
		float[] buffer = new float[samples];
		for (int i = 0; i < samples; i++) {
			buffer[i] = amplitude * wavetable.getSample(oscPhase, oscFreq);
			oscPhase += oscFreq;
			if (oscPhase >= wavetable.tableSize) oscPhase -= wavetable.tableSize;
		}
		return buffer;
	}

	public float[] get(int duration, float[] envelope, float frequency) {
		int samples = duration * Settings.samplingRate / 1000;
		float oscAmp = 0;
		float oscFreq = frequency * frequencyScale;
		float oscPhase = 0;
		float envelopeIndex = 0;
		float envelopeIncrement = (float) envelope.length / samples;
		float[] buffer = new float[samples];
		for (int i = 0; i < samples; i++) {
			oscAmp = envelope[(int) envelopeIndex];
			buffer[i] = oscAmp * wavetable.getSample(oscPhase, oscFreq);
			envelopeIndex += envelopeIncrement;
			oscPhase += oscFreq;
			if (oscPhase >= wavetable.tableSize) oscPhase -= wavetable.tableSize;
		}
		return buffer;
	}

	public float[] get(int duration, float amplitude, float[] glide) {
		int samples = duration * Settings.samplingRate / 1000;
		float oscFreq = 0;
		float oscPhase = 0;
		float glideIndex = 0;
		float glideIncrement = (float) glide.length / samples;
		float[] buffer = new float[samples];
		for (int i = 0; i < samples; i++) {
			oscFreq = glide[(int) glideIndex] * frequencyScale;
			buffer[i] = amplitude * wavetable.getSample(oscPhase, oscFreq);
			glideIndex += glideIncrement;
			oscPhase += oscFreq;
			if (oscPhase >= wavetable.tableSize) oscPhase -= wavetable.tableSize;
		}
		return buffer;
	}

	public float[] get(int duration, float[] envelope, float[] glide) {
		int samples = duration * Settings.samplingRate / 1000;
		float oscAmp = 0;
		float oscFreq = 0;
		float oscPhase = 0;
		float envelopeIndex = 0;
		float envelopeIncrement = (float) envelope.length / samples;
		float glideIndex = 0;
		float glideIncrement = (float) glide.length / samples;
		float[] buffer = new float[samples];
		for (int i = 0; i < samples; i++) {
			oscAmp = envelope[(int) envelopeIndex];
			oscFreq = glide[(int) glideIndex] * frequencyScale;
			buffer[i] = oscAmp * wavetable.getSample(oscPhase, oscFreq);
			envelopeIndex += envelopeIncrement;
			glideIndex += glideIncrement;
			oscPhase += oscFreq;
			if (oscPhase >= wavetable.tableSize) oscPhase -= wavetable.tableSize;
		}
		return buffer;
	}

	public void setFrequency(float frequency) {
		this.frequency = frequency * frequencyScale;
	}

	@Override
	public void startFlow() {
		new Thread(flow).start();
	}

	@Override
	public void stopFlow() {
		flow.quit();
	}

	@Override
	public ByteBuffer getVector() {
		Arrays.fill(mixVector, 0);
		buffer.clear();
		for (int i = 0; i < mixVector.length; i++) {
			mixVector[i] = amplitude * wavetable.getSample(runningPhase, frequency);
			runningPhase += frequency;
			if (runningPhase >= wavetable.tableSize) runningPhase -= wavetable.tableSize;
		}
		processMasterEffects();
		for (int i = 0; i < mixVector.length; i++) buffer.putShort((short) (mixVector[i] * Short.MAX_VALUE));
		return buffer;
	}

	@Override
	public void processMasterEffects() {}

}

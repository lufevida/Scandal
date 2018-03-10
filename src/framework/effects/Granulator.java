package framework.effects;

import java.util.ArrayList;

import framework.generators.AudioTask;
import framework.generators.WaveFile;
import framework.generators.WindowFunction;
import framework.utilities.Settings;

public class Granulator {

	public float mix = 1;
	private int playbackPosition = 0;
	private int playbackDeviation = 512;
	public float playbackSpeed = 1;
	private int grainLength = 4410;
	public int interGrainTime = 44;
	private final int grainCount = 128;
	public int bufferLength = Settings.samplingRate * 2;
	private final float[] captureBuffer = new float[bufferLength];
	private int captureBufferIndex = 0;
	private final int windowSize = 8192;
	private double windowIncrement = (double) windowSize / grainLength;
	private final float[] window = new WindowFunction(windowSize).get();
	private final ArrayList<Grain> grainArray = new ArrayList<>();
	private int igtCounter = 0;
	private int grainArrayCounter = 0;
	private float grainSample;
	private final float timeScale = Settings.samplingRate * 0.001f;
	
	class Grain {
		
		boolean isBusy;
		private float index;
		private float sample;
		private double windowIndex;
		
		Grain() {
			reset();
		}
		
		void reset() {
			isBusy = false;
			windowIndex = 0;
			index = playbackPosition + ((float) Math.random() * playbackDeviation);
			if (index >= bufferLength) index -= bufferLength;
            if (index < 0) index += bufferLength;
		}
		
		float getSample() {
			sample = captureBuffer[(int) index] * window[(int) windowIndex] * 0.1f;
			index += playbackSpeed;
			if (index >= bufferLength) index -= bufferLength;
			windowIndex += windowIncrement;
			if (windowIndex >= windowSize) reset();
			return sample;
		}
		
	}
	
	public Granulator() {
		for (int i = 0; i < grainCount; i++) grainArray.add(i, new Grain());
		grainArray.get(0).isBusy = true;
	}
	
	public void setPosition(int position) {
		this.playbackPosition = position;
		for (Grain grain : grainArray) if (!grain.isBusy) grain.reset();
	}
	
	public void setDeviation(int deviation) {
		this.playbackDeviation = deviation;
		for (Grain grain : grainArray) if (!grain.isBusy) grain.reset();
	}
	
	public void setGrainLength(int grainLength) {
		this.grainLength = grainLength;
		windowIncrement = (double) windowSize / grainLength;
	}
	
	private void updateGrainArray() {
		igtCounter++;
		if (igtCounter >= interGrainTime) {
			igtCounter -= interGrainTime;
			grainArray.get(grainArrayCounter).isBusy = true;
			grainArrayCounter++;
			if (grainArrayCounter >= grainCount) grainArrayCounter = 0;
		}
	}
	
	public float[] processVector(float[] buffer) {
		for (int i = 0; i < buffer.length; i++) {
			captureBuffer[captureBufferIndex++] = buffer[i];
			if (captureBufferIndex >= bufferLength) captureBufferIndex = 0; 
			grainSample = 0;
			for (Grain grain : grainArray) if (grain.isBusy) grainSample += grain.getSample();
			buffer[i] = (1.0f - mix) * buffer[i] + mix * grainSample;
			updateGrainArray();
		}
		return buffer;
	}

	public float[] process(float[] buffer, float position, float deviation, float speed, float grainLength, float interGrainTime) {
		if (position + deviation + grainLength >= buffer.length) return buffer;
		if (position - deviation < 0) return buffer;
		if (interGrainTime <= 0) return buffer;
		position *= timeScale;
		deviation *= timeScale;
		grainLength *= timeScale;
		interGrainTime *= timeScale;
		float[] processedBuffer = new float[buffer.length];
		float grainCount = (buffer.length - grainLength) / interGrainTime;
		float bufferIndex;
		float grainIndex;
		float[] window = new WindowFunction((int) grainLength).get();
		for (int i = 0; i < grainCount; i++) {
			bufferIndex = i * interGrainTime;
			grainIndex = position + (((float) Math.random() * 2 - 1) * deviation);
			for (int j = 0; j < (int) grainLength; j++) {
				processedBuffer[(int) bufferIndex++] += buffer[(int) grainIndex] * window[j];
				grainIndex += speed;
				if (grainIndex >= buffer.length)
					grainIndex = position + (((float) Math.random() * 2 - 1) * deviation);
			}
		}
		return processedBuffer;
	}
	
	public static void main(String[] args) throws Exception {
		float[] lisa = new WaveFile("doc/monoLisa.wav").getMonoSum();
		float position = 5000;
		float deviation = 50;
		float speed = 1.0f;
		float grainLength = 500;
		float interGrainTime = 100f;
		float[] granulatedLisa = new Granulator().process(lisa, position, deviation, speed, grainLength, interGrainTime);
		new AudioTask().playMono(new Gain().process(granulatedLisa, 0.2f));
	}

}

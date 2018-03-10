package framework.effects;

import framework.generators.AudioTask;
import framework.generators.BreakpointFunction;
import framework.generators.WaveFile;

public class Gain {
	
	public float[] process(float[] buffer, int gain) {
		return process(buffer, (float) gain);
	}
	
	public float[] process(float[] buffer, float gain) {
		float[] processedBuffer = new float[buffer.length];
		for (int i = 0; i < buffer.length; i++) {
			processedBuffer[i] = gain * buffer[i];
		}
		return processedBuffer;
	}

	public float[] process(float[] buffer, float[] gains) {
		float[] processedBuffer = new float[buffer.length];
		double gainIndex = 0;
		double gainIncrement = (double) gains.length / buffer.length;
		for (int i = 0; i < buffer.length; i++) {
			processedBuffer[i] = gains[(int) gainIndex] * buffer[i];
			gainIndex += gainIncrement;
		}
		return processedBuffer;
	}
	
	public static void main(String[] args) throws Exception {
		float[] lisa = new WaveFile("doc/test.wav").getMonoSum();
		float[] envelope = new BreakpointFunction(200, new float[]{0.0f, 1.0f, 0.0f}).get();
		float[] gain = new Gain().process(lisa, envelope);
		new AudioTask().playMono(gain);
	}

}

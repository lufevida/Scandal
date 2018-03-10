package framework.effects;

import framework.generators.AudioTask;
import framework.generators.WaveFile;

public class Reverse {
	
	public float[] process(float[] buffer) {
		float[] processedBuffer = new float[buffer.length];
		for (int i = 1; i <= buffer.length; i++) {
			processedBuffer[i - 1] = buffer[buffer.length - i];
		}
		return processedBuffer;
	}
	
	public static void main(String[] args) throws Exception {
		float[] lisa = new WaveFile("doc/monoLisa.wav").getMonoSum();
		float[] reverse = new Reverse().process(lisa);
		new AudioTask().playMono(reverse);
	}

}

package framework.effects;

import framework.generators.AudioTask;
import framework.generators.WaveFile;

public class Loop {

	public float[] process(float[] buffer, int start, int end) {
		int count = buffer.length / (end - start);
		return process(buffer, start, end, count); // try to preserve the duration
	}

	public float[] process(float[] buffer, int start, int end, int count) {
		int samples = (end - start) * count;
		float[] processedBuffer = new float[samples];
		for (int i = 0, j = 0; i < samples; i++, j++) {
			processedBuffer[i] = buffer[j % end + start];
		}
		return processedBuffer;
	}
	
	public static void main(String[] args) throws Exception {
		float[] lisa = new WaveFile("doc/monoLisa.wav").getMonoSum();
		float[] loop = new Loop().process(lisa, 0, 10000, 8);
		new AudioTask().playMono(loop);
	}

}

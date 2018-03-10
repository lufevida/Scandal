package framework.effects;

import framework.generators.AudioTask;
import framework.generators.BreakpointFunction;
import framework.generators.WaveFile;

public class Splice {
	
	// TODO: splice with cross fades

	public float[] process(float[]... buffers) {
		int samples = 0;
		for (float[] buffer : buffers) samples += buffer.length;
		float[] processedBuffer = new float[samples];
		for (int i = 0, j = 0, k = 0; i < samples; i++, k++) {
			processedBuffer[i] = buffers[j][k];
			if (k == buffers[j].length - 1) {
				k = 0;
				if (j < buffers.length - 1) j += 1;
			}
		}
		return processedBuffer;
	}

	public float[] crossFade(float[] first, float second[], int overlap) {
		int samples = first.length + second.length - overlap;
		float[] fadeFirst = fadeOut(first, overlap);
		float[] fadeSecond = fadeIn(second, overlap);
		float[] returnBuffer = new float[samples];
		for (int i = 0; i < first.length; i++) returnBuffer[i] = fadeFirst[i];
		for (int i = 0; i < second.length; i++) returnBuffer[i + first.length - overlap] += fadeSecond[i];
		//for (int i = first.length - overlap; i < first.length; i++) returnBuffer[i] *= 0.5f;
		//for (int i = 0; i < samples; i++) System.out.println(returnBuffer[i]);
		return returnBuffer;
	}
	
	public float[] fadeIn(float[] buffer, int duration) {
		float[] returnBuffer = buffer;
		float[] fade = new BreakpointFunction(duration, new float[]{0, 1}).get();
		for (int i = 0; i < duration; i++) returnBuffer[i] *= fade[i];		
		return returnBuffer;
	}
	
	public float[] fadeOut(float[] buffer, int duration) {
		float[] returnBuffer = buffer;
		float[] fade = new BreakpointFunction(duration, new float[]{1, 0}).get();
		for (int i = 0; i < duration; i++) returnBuffer[i + buffer.length - duration] *= fade[i];		
		return returnBuffer;
	}
	
	public static void main(String[] args) throws Exception {
		float[] lisa = new WaveFile("doc/monoLisa.wav").getMonoSum();
		float[] loop1 = new Loop().process(lisa, 0, 12000, 8);
		float[] loop2 = new Loop().process(lisa, 0, 6000, 16);
		float[] loop3 = new Loop().process(lisa, 0, 3000, 16);
		float[] loop4 = new Loop().process(lisa, 0, 6000, 8);
		float[] splice = new Splice().process(loop1, loop2, loop3, loop4);
		new AudioTask().playMono(splice);
	}

}

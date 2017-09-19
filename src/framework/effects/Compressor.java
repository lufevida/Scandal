package framework.effects;

import framework.generators.AudioTask;
import framework.generators.WaveFile;

public class Compressor {
	
	public float[] process(float[] buffer, float threshold, float ratio) {
		for (int i = 0; i < buffer.length; i++)
			if (buffer[i] * dBFSToRaw(-Math.abs(ratio)) > dBFSToRaw(-Math.abs(threshold))) buffer[i] *= dBFSToRaw(-1);
		return buffer;
	}
	
	static float rawTodBFS(float amplitude) {
		return 20 * (float) Math.log10(amplitude);
	}
	
	static float dBFSToRaw(float amplitude) {
		return (float) Math.pow(10, amplitude * 0.05);
	}
	
	public static void main(String[] args) throws Exception {
		float[] lisa = new WaveFile("doc/monoLisa.wav").getMonoSum();
		new AudioTask().playMono(new Compressor().process(lisa, 3, 2));
	}

}

package framework.effects;

import framework.generators.AudioTask;
import framework.generators.WaveFile;
import framework.utilities.Functions;

public class Compressor {
	
	// TODO: add attack, release, Q, and make it real-time
	
	public static void main(String[] args) throws Exception {
		float[] lisa = new WaveFile("doc/monoLisa.wav").getMonoSum();
		new AudioTask().playMono(new Compressor().process(lisa, -12, 4));
	}
	
	public float[] process(float[] buffer, float threshold, float ratio) {
		int count = 0;
		float[] result = new float[buffer.length];
		for (int i = 0; i < buffer.length; i++) {
			float db = Functions.rawTodBFS(buffer[i]);
			if (db > threshold) {
				count++;
				float diff = Functions.distance(db, threshold);
				result[i] = Functions.dBFSToRaw(threshold + diff / ratio, buffer[i] < 0);
			}
			else result[i] = buffer[i];
		}
		System.out.println(count + " samples compressed.");
		return result;
	}

}

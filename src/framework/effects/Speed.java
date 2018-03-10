package framework.effects;

import framework.generators.AudioTask;
import framework.generators.WaveFile;

public class Speed {

	public float[] process(float[] buffer, float speed) {
		if (speed == 0 || speed == 1) return buffer;
		if (speed < 0) speed = -speed;
		int samples = (int) (buffer.length / speed);
		float[] processedBuffer = new float[samples];
		double speedIndex = 0;
		double x0, x1, y0, y1;
		for (int i = 0; i < samples; i++) {
			x0 = Math.floor(speedIndex);
			x1 = Math.ceil(speedIndex);
			y0 = buffer[(int) x0];
			y1 = buffer[x1 < buffer.length ? (int) x1 : 0];
			processedBuffer[i] = (float) (y0 + (speedIndex - x0) * (y1 - y0));
			if (processedBuffer[i] >= 1) System.out.println(processedBuffer[i]);
			//processedBuffer[i] = buffer[(int) speedIndex];
			speedIndex += speed;
			if (speedIndex >= buffer.length) speedIndex -= buffer.length;
		}
		return processedBuffer;
	}
	
	public static void main(String[] args) throws Exception {
		WaveFile lisa = new WaveFile("doc/monoLisa.wav");
		lisa.printInfo();
		float[] speed = new Speed().process(lisa.getMonoSum(), 1.2f);
		new AudioTask().playMono(speed);
	}

}

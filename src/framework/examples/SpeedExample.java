package framework.examples;

import framework.effects.Speed;
import framework.generators.AudioTask;
import framework.generators.WaveFile;

public class SpeedExample {

	public static void main(String[] args) throws Exception {
		WaveFile lisa = new WaveFile("/Users/luisfelipe/Desktop/Audio Units/Samples/StringsPlucked/Harp/HarpC4.wav");
		lisa.printInfo();
		float[] speed = new Speed().process(lisa.getMonoSum(), 1.2);
		AudioTask task = new AudioTask();
		task.playMono(speed);
	}

}

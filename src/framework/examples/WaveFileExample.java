package framework.examples;

import framework.generators.AudioTask;
import framework.generators.WaveFile;

public class WaveFileExample {

	public static void main(String[] args) throws Exception {
		WaveFile lisa = new WaveFile("/Users/luisfelipe/Desktop/Audio Units/Samples/Crotales/Bow/CrotalesBowD7.wav");
		lisa.printInfo();
		new AudioTask().playMono(lisa.getMonoSum());
	}
	
}

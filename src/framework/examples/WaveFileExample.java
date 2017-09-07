package framework.examples;

import framework.generators.AudioTask;
import framework.generators.WaveFile;

public class WaveFileExample {

	public static void main(String[] args) throws Exception {
		WaveFile lisa = new WaveFile("/Users/luisfelipe/Dropbox/Samples/Banjo/banjo60f.wav");
		new AudioTask().playMono(lisa.getMonoSum());
		lisa.exportText("/Users/luisfelipe/Dropbox/Samples/Banjo/banjo60f.hpp", 44100);
	}

}

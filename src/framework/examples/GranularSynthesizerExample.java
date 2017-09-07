package framework.examples;

import framework.generators.AudioFlow;
import framework.generators.GranularSynthesizer;
import framework.generators.WaveFile;
import framework.utilities.Settings;

public class GranularSynthesizerExample {

	public static void main(String[] args) throws Exception {
		float[] lisa = new WaveFile("doc/monoLisa.wav").getMonoSum();
		GranularSynthesizer synth = new GranularSynthesizer(Settings.midiController, lisa, 370.0f);
		AudioFlow flow = synth.start();
		Thread.sleep(30000);
		flow.quit();
		synth.close();
		System.exit(0);
	}

}

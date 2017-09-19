package framework.examples;

import framework.generators.AudioFlow;
import framework.generators.GranularSynthesizer;
import framework.utilities.Settings;
import framework.waveforms.WavetableCosine;

public class GranularSynthesizerExample {

	public static void main(String[] args) throws Exception {
		//float[] lisa = new WaveFile("doc/monoLisa.wav").getMonoSum();
		//GranularSynthesizer synth = new GranularSynthesizer(Settings.midiController, lisa, 370.0f);
		GranularSynthesizer synth = new GranularSynthesizer(Settings.midiController, new WavetableCosine());
		AudioFlow flow = synth.start();
		Thread.sleep(60000);
		flow.quit();
		synth.close();
		System.exit(0);
	}

}

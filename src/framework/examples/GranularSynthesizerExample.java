package framework.examples;

import framework.generators.GranularSynthesizer;
import framework.utilities.Settings;
import framework.waveforms.ClassicSawtooth;

public class GranularSynthesizerExample {
	
	static GranularSynthesizer synth;

	public static void main(String[] args) throws Exception {
		//float[] lisa = new WaveFile("doc/monoLisa.wav").getMonoSum();
		//GranularSynthesizer synth = new GranularSynthesizer(Settings.midiController, lisa, 370.0f);
		synth = new GranularSynthesizer(Settings.midiController, new ClassicSawtooth());
		synth.startFlow();
		Thread.sleep(10000);
		synth.stopFlow();
	}

}

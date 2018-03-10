package framework.examples;

import framework.generators.PolyphonicSynthesizer;
import framework.utilities.Settings;
import framework.waveforms.ClassicSawtooth;

public class PolyphonicSynthesizerExample {
	
	static PolyphonicSynthesizer synth;

	public static void main(String[] args) throws Exception {
		synth = new PolyphonicSynthesizer(Settings.midiController, new ClassicSawtooth());
		synth.startFlow();
		Thread.sleep(10000);
		synth.stopFlow();
	}

}

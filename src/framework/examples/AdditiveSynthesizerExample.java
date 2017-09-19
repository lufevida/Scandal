package framework.examples;

import framework.generators.AdditiveSynthesizer;
import framework.generators.AudioFlow;
import framework.utilities.Settings;
import framework.waveforms.ClassicSawtooth;

public class AdditiveSynthesizerExample {

	public static void main(String[] args) throws Exception {
		AdditiveSynthesizer synth = new AdditiveSynthesizer(Settings.midiController, new ClassicSawtooth());
		AudioFlow flow = synth.start();
		Thread.sleep(60000);
		flow.quit();
		synth.close();
		System.exit(0);		
	}

}

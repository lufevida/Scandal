package language.ide.widget;

import framework.generators.GranularSynthesizer;
import framework.waveforms.ClassicSawtooth;

public class GranularSynth extends WidgetTab {

	public GranularSynth() throws Exception {
		super("Granular Synthesizer", new GranularSynthesizer(new ClassicSawtooth()));
		//new GranularSynthesizer(new WaveFile("doc/monoLisa.wav").getMonoSum(), 370.0f);
	}

}

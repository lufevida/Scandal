package language.ide;

import framework.generators.PolyphonicSynthesizer;
import framework.waveforms.ClassicSawtooth;

public class ClassicSynth extends WidgetTab {

	public ClassicSynth() throws Exception {
		super("Classic Synthesizer", new PolyphonicSynthesizer(new ClassicSawtooth()));
	}

}

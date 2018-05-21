package language.ide.widget;

import framework.generators.AdditiveSynthesizer;
import framework.generators.AdditiveSynthesizer.Series;
import framework.waveforms.ClassicSawtooth;

public class AdditiveSynth extends WidgetTab {

	public AdditiveSynth() throws Exception {
		super("Additive Synthesizer", new AdditiveSynthesizer(new ClassicSawtooth(), Series.BELL, 2));
		addSlider("Attack", 44, 44100, 882, "samples",
				(obs, old, val) -> ((AdditiveSynthesizer) synth).attackSamples = val.intValue());
		addSlider("Decay", 44, 44100, 2205, "samples",
				(obs, old, val) -> ((AdditiveSynthesizer) synth).decaySamples = val.intValue());
		addSlider("Sustain", 0, 1, 0.5f, "",
				(obs, old, val) -> ((AdditiveSynthesizer) synth).sustainLevel = val.floatValue());
		addSlider("Release", 44, 88200, 410, "samples",
				(obs, old, val) -> ((AdditiveSynthesizer) synth).releaseSamples = val.intValue());
		addSlider("Granulator Mix", 0, 1, 0.5f, "",
				(obs, old, val) -> ((AdditiveSynthesizer) synth).granulator.mix = val.floatValue());
	}

}

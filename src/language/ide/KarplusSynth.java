package language.ide;

import framework.generators.KarplusStrong;

public class KarplusSynth extends WidgetTab {

	public KarplusSynth() throws Exception {
		super("Plectrum", new KarplusStrong());
		addSlider("Tremolo Speed", 0, 20, 4, "Hz",
                (obs, old, val) -> ((KarplusStrong) synth).tremoloSpeed = val.floatValue());
        addSlider("Delay Time", 0, 1000, 500, "ms",
                (obs, old, val) -> ((KarplusStrong) synth).delayTime = val.intValue());
	}

}

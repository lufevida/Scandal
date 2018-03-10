package framework.examples;

import framework.generators.KarplusStrong;
import framework.utilities.GenericView;
import framework.utilities.Settings;

public class KarplusStrongExample extends GenericView {

	@Override
	public void init() throws Exception {
		synth = new KarplusStrong(Settings.midiController);
	}

	@Override
	public void addParameters() {
		title = "Karplus-Strong Synthesizer";
		addSlider("Tremolo Speed", 0, 20, 4, "Hz", (obs, old, val) -> ((KarplusStrong) synth).tremoloSpeed = val.floatValue());
		addSlider("Delay Time", 0, 1000, 500, "ms", (obs, old, val) -> ((KarplusStrong) synth).delayTime = val.intValue());
	}
	
	public static void main(String[] args) throws Exception {
		launch(args);
	}

}

package language.ide.widget;

import framework.effects.Granulator;
import framework.generators.WavePlayer;
import framework.utilities.Settings;

public class GranulatorEffect extends WidgetTab {
	
	private static class MoonshineDSP extends WavePlayer {

        public final Granulator granulator = new Granulator();

        public MoonshineDSP(String path) throws Exception {
            super(path);
        }

        @Override
        public void processMasterEffects() {
            mixVector = granulator.processVector(mixVector);
        }

    }

	public GranulatorEffect() throws Exception {
		super("Moonshine", new MoonshineDSP("wav/monoLisa.wav"));
        MoonshineDSP source = (MoonshineDSP) synth;
        addSlider("Playback Volume", 0, 100, 50, "%%",
                (obs, old, val) -> source.masterVolume = val.floatValue() * 0.01f);
        addSlider("Playback Speed", 50, 200, 100, "%%",
                (obs, old, val) -> source.playbackSpeed = val.floatValue() * 0.01f);
        addSlider("Buffer Length", 0, 2000, 500, "ms",
                (obs, old, val) -> {
            float value = val.floatValue() * Settings.samplingRate / 1000.0f;
            source.granulator.bufferLength = (int) value;
        });
        addSlider("Playback Position", 0, 100, 0, "%%",
                (obs, old, val) -> {
            float value = source.granulator.bufferLength * val.floatValue() * 0.01f;
            source.granulator.setPosition((int) value);
        });
        addSlider("Playback Deviation", 0, 100, 5, "%%",
                (obs, old, val) -> {
            float value = source.granulator.bufferLength * val.floatValue() * 0.01f;
            source.granulator.setDeviation((int) value);
        });
        addSlider("Grain Speed", 50, 200, 100, "%%",
                (obs, old, val) -> source.granulator.playbackSpeed = val.floatValue() * 0.01f);
        addSlider("Grain Length", 0, 100, 50, "%%",
                (obs, old, val) -> {
            float value = source.granulator.bufferLength * val.floatValue() * 0.01f;
            source.granulator.setGrainLength((int) value);
        });
        addSlider("Grain Interval", 0, 1000, 40, "ms",
                (obs, old, val) -> {
            float value = val.floatValue() * Settings.samplingRate / 1000.0f;
            source.granulator.interGrainTime = (int) value;
        });
	}

}

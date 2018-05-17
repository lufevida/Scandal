package language.ide;

import framework.generators.WavePlayer;

public class WaveTab extends PlotTab {

	private final WavePlayer player;

	public WaveTab(String title, float[] buffer, int channels) throws Exception {
		super(title, buffer, 1000);
		player = new WavePlayer(buffer);
		player.startFlow();
		setOnClosed(e -> pause());
	}

	public void pause() {
		player.stopFlow();
	}

}

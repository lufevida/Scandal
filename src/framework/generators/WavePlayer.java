package framework.generators;

import java.nio.ByteBuffer;
import java.util.Arrays;

import framework.utilities.Settings;

public class WavePlayer implements RealTimePerformer {
	
	private final float[] fileBuffer;
	private float runningIndex = 0;
	public float[] mixVector = new float[Settings.vectorSize];
	private final ByteBuffer buffer = ByteBuffer.allocate(Settings.vectorSize * Settings.bitDepth / 8);
	private final AudioFlow flow = new AudioFlow(this, Settings.mono);
	public float masterVolume = 0.5f;
	public float playbackSpeed = 1.0f;
	
	public WavePlayer(String path) throws Exception {
		fileBuffer = new WaveFile(path).getMonoSum();
	}
	
	@Override
	public void startFlow() {
		new Thread(flow).start();
	}

	@Override
	public void stopFlow() {
		flow.quit();
	}

	@Override
	public ByteBuffer getVector() {
		Arrays.fill(mixVector, 0);
		buffer.clear();
		for (int i = 0; i < mixVector.length; i++) {
			mixVector[i] = masterVolume * fileBuffer[(int) runningIndex];
			runningIndex += playbackSpeed;
			if (runningIndex >= fileBuffer.length) runningIndex -= fileBuffer.length; // loop
		}
		processMasterEffects();
		for (int i = 0; i < mixVector.length; i++) buffer.putShort((short) (mixVector[i] * Short.MAX_VALUE));
		return buffer;
	}

	@Override
	public void processMasterEffects() {}

}

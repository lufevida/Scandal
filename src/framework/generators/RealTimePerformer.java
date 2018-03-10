package framework.generators;

import java.nio.ByteBuffer;

public interface RealTimePerformer {
	
	public abstract void startFlow();
	
	public abstract void stopFlow();
	
	public abstract ByteBuffer getVector();
	
	public abstract void processMasterEffects();

}

package framework.generators;

public abstract class Function {

	public final int length;
	public final float twoPi = (float) Math.PI * 2;
	
	public Function(int length) {
		this.length = length;
	}
	
	public abstract float[] get();

}

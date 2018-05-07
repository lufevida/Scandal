package language.compiler;

import java.util.function.Function;

public class Scandal implements Runnable {

	// TODO change all counters to double to increase precision
	
	/*static Function<float[], Float> max = buffer -> {
		float val = Float.NEGATIVE_INFINITY;
		for (int i = 0; i < buffer.length; i++) if (val < buffer[i]) val = buffer[i];
		return val;
	};*/

	static Function<Float, Float> decrement = x -> x - 1;
//	static Function<Float, Float> negate = x -> -x;
//	static Function<Float, Float> add5 = x -> x + 5;
//	static Function<Float, Float> times9 = x -> x * 9;
	static Function<Float, Float> composite = x -> decrement.apply(x);
	
	public static void main(String[] args) throws Exception {
		Compiler c = new Compiler("doc/Lambda.scandal");
		c.compile();
		c.print();
		c.getInstance().run();
	}

	public void run() {
		//Function<Float, Float> twice = decrement.andThen(decrement);
	}

}

package language.compiler;

import java.util.function.Function;

public class Scandal implements Runnable {

	// TODO change all counters to double to increase precision
	
	static Function<float[], Float> max = buffer -> {
		float val = Float.NEGATIVE_INFINITY;
		for (int i = 0; i < buffer.length; i++) if (val < buffer[i]) val = buffer[i];
		return val;
	};

	public static void main(String[] args) throws Exception {
		Compiler c = new Compiler("src/language/prelude/Lambda.scandal");
		c.compile();
		c.print();
		c.getInstance().run();
	}

	public void run() {}

}

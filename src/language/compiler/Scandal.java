package language.compiler;

public class Scandal {

	// TODO change all counters to double to increase precision
	
	//static ObjectObject test = x -> (ObjectObject) y -> (ObjectObject) z -> ((float[]) x)[0] - ((float[]) y)[0] - ((float[]) z)[0];
	//static Function<Float, Function<Float, Float>> test = x -> y -> x - y;
	//static Function<Integer, Function<float[], float[]>> floatToArrayToArray = x -> y -> new float[x];
	//static Function<float[], float[]> arrayToArray = x -> x;
	
	public static void main(String[] args) throws Exception {
		//new Compiler(args[0]).run();
		Compiler c = new Compiler("doc/Scandalous_Concrete.scandal");
		//c.print();
		c.run();
		//int thing = (int) ((ObjectObject) test.apply(13)).then((ObjectObject) test.apply(12)).then((ObjectObject) test.apply(13)).apply(117);
		//System.out.println(thing == 13 - (12 - (13 - 117)));
		//float thing = test.apply(13.0f).andThen(test.apply(12.0f)).andThen(test.apply(13.0f)).apply(117.0f);
		//System.out.println(thing == 13 - (12 - (13 - 117)));
		//float[] composite = floatToArrayToArray.apply(10).andThen(arrayToArray).apply(new float[22]);
	}

}

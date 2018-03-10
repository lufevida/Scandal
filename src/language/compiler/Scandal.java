package language.compiler;

public class Scandal {
	
	// TODO reorder bytecode generation so that print statements are executed in order
	// TODO change all counters to double to increase precision

	public static void main(String[] args) throws Exception {
		//new Compiler(args[0]).run();
		Compiler c = new Compiler("doc/Lambda.scandal");
		c.print();
		c.run();
	}

}

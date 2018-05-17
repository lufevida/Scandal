package language.compiler;

public class Scandal {

	public static void main(String[] args) throws Exception {
		Compiler c = new Compiler(args[0]);
		c.compile();
		c.getInstance().run();
	}

}